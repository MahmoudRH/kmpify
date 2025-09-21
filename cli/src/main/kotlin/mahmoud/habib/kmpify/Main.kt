package mahmoud.habib.kmpify

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import com.github.ajalt.mordant.terminal.prompt
import mahmoud.habib.kmpify.core.MigrationManager
import mahmoud.habib.kmpify.core.sumOf
import mahmoud.habib.kmpify.model.MigrationFileRow
import mahmoud.habib.kmpify.model.MigrationSummary
import mahmoud.habib.kmpify.utils.printAsTree
import mahmoud.habib.kmpify.utils.printMigrationSummary
import mahmoud.habib.mahmoud.habib.kmpify.model.MigrationConfigs
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.system.exitProcess

class KMPifyCli : SuspendingCliktCommand("kmpify") {


    private companion object {
        const val HELP_INPUT_PATH = "The directory containing your Android .kt files, typically your main/java/ folder."
        const val HELP_OUTPUT_PATH =
            "The directory where the migrated files will be saved. Usually points to your KMP commonMain/kotlin/ directory."
        const val HELP_KMP_PROJECT_NAME = "The name of your multiplatform project (used to build resource paths)."
        const val HELP_SHARED_MODULE = "The name of your shared module. Defaults to 'composeapp' if left empty."
        const val HELP_CUSTOM_PREVIEW = "If you're using a custom @Preview annotation, you can specify it here."
        const val HELP_DRY_RUN =
            "If true, KMPify will simulate the migration and show what would be changed, but won't overwrite any files."
        const val DEFAULT_SHARED_MODULE = "composeapp"
        const val DEFAULT_DRY_RUN = true

        const val WELCOME_TO_KMPify = """
                    +===================================================================+
                    |  ____      ____      __                               _           |
                    | |_  _|    |_  _|    [  |                             / |_         |
                    |   \ \  /\  / /.---.  | |  .---.  .--.  _ .--..--.   `| |-' .--.   |
                    |    \ \/  \/ // /__\\ | | / /'`\] .'`\ [ `.-. .-. |   | | / .'`\ \ |
                    |     \  /\  / | \__., | | | \__.| \__. || | | | | |   | |,| \__. | |
                    |      \/  \/   '.__.'[___]'.___.''.__.'[___||__||__]  \__/ '.__.'  |
                    |            ___  ____  ____    ____ _______  _     ___             |
                    |           |_  ||_  _||_   \  /   _|_   __ \(_)  .' ..]            |
                    |             | |_/ /    |   \/   |   | |__) |_  _| |_  _   __      |
                    |             |  __'.    | |\  /| |   |  ___[  |'-| |-'[ \ [  ]     |
                    |            _| |  \ \_ _| |_\/_| |_ _| |_   | |  | |   \ '/ /      |
                    |           |____||____|_____||_____|_____| [___][___][\_:  /       |
                    |                                                      \__.'        |
                    +===================================================================+
            """
    }

    private val terminal = Terminal()

    private val inputPath by option("--input", "-i")
        .path(mustExist = true, canBeFile = true, canBeDir = true)
        .help(HELP_INPUT_PATH)

    private val outputPath by option("--output", "-o")
        .path(mustExist = true, canBeFile = false, canBeDir = true)
        .help(HELP_OUTPUT_PATH)

    private val kmpProjectName by option("--project", "-p")
        .help(HELP_KMP_PROJECT_NAME)

    // Shared module with default
    private val sharedModule by option("--shared", "-s")
        .help(HELP_SHARED_MODULE)

    // Custom preview (optional)
    private val customPreview by option("--preview")
        .help(HELP_CUSTOM_PREVIEW)

    // Dry run flag
    private val dryRun by option("--dry-run", "-d")
        .flag()
        .help(HELP_DRY_RUN)

    override suspend fun run() {
        println(brightBlue(WELCOME_TO_KMPify))

        val resolvedInputPath = promptForInputPath()
        val detectedFiles = detectKotlinFiles(resolvedInputPath)

        if (!confirmDetectedFiles(detectedFiles)) {
            println(red("Migration cancelled by user"))
            return
        }

        val resolvedOutputPath = promptForOutputPath()
        val resolvedProjectName = promptForProjectName()
        val resolvedSharedModule = promptForSharedModule()
        val resolvedCustomPreview = promptForCustomPreview()
        val resolvedDryRun = promptForDryRun()

        println(blue("Migration Started.."))

        runMigration(
            ktFiles = detectedFiles,
            inputDirectory = resolvedInputPath.pathString,
            projectName = resolvedProjectName,
            sharedModuleName = resolvedSharedModule,
            customPreview = resolvedCustomPreview ?: "",
            isDryRun = resolvedDryRun,
            outputDirectory = resolvedOutputPath.pathString,
        ).let { (changes, summary) ->

            printMigrationSummary(report = summary)
            println(brightGreen("Migration Completed.."))
            val printChangesTree = terminal.prompt("\n See changes on Tree? (y,n) ")
            if (printChangesTree.equals("y", true)) {
                printAsTree(changes)
            }

        }

    }

    private fun promptForInputPath(): Path {
        return inputPath ?: run {
            var path: Path? = null
            while (path == null) {
                val input = terminal.prompt("Input path") ?: continue
                val candidatePath = Path(input)
                if (input.isBlank()) {
                    println(red("Input path is required"))
                } else if (!candidatePath.exists()) {
                    println("Path does not exist: ${red(input)}")
                } else {
                    path = candidatePath
                }
            }
            path
        }
    }

    private fun promptForOutputPath(): Path {
        return outputPath ?: run {
            var path: Path? = null
            while (path == null) {
                val input = terminal.prompt("Output path") ?: continue
                val candidatePath = Path(input)
                when {
                    !candidatePath.exists() -> {
                        terminal.println("Path does not exist: ${red(input)}")
                        val createChoice = terminal.prompt("Would you like to create this directory? (y/n)") ?: "n"
                        if (createChoice.equals("y", true)) {
                            try {
                                Files.createDirectories(candidatePath)
                                terminal.println(green("Created directory: $input"))
                                path = candidatePath
                            } catch (e: Exception) {
                                terminal.println(red("Failed to create directory: ${e.message}"))
                            }
                        } else {
                            println(red("no output path provided"))
                            exitProcess(1)
                        }
                    }

                    !candidatePath.isDirectory() -> terminal.println("Path must be a directory: ${red(input)}")
                    else -> path = candidatePath
                }
            }
            path
        }
    }

    private fun promptForProjectName(): String {
        return kmpProjectName ?: run {
            var name: String? = null
            while (name == null) {
                val input = terminal.prompt("KMP Project Name") ?: continue
                if (input.isNotBlank()) {
                    name = input
                } else {
                    terminal.println(red("Project name cannot be empty"))
                }
            }
            name
        }
    }

    private fun promptForSharedModule(): String {
        return sharedModule ?: run {
            val input = terminal.prompt("Shared Module Name ${cyan("(default: $DEFAULT_SHARED_MODULE)")}")
            if (input.isNullOrBlank()) DEFAULT_SHARED_MODULE else input
        }
    }

    private fun promptForCustomPreview(): String? {
        return customPreview ?: run {
            terminal.prompt("Custom Preview annotation (optional)")
                ?.takeIf { it.isNotBlank() }
        }
    }

    private fun promptForDryRun(): Boolean {
        return if (dryRun) true else {
            YesNoPrompt("Dry Run", terminal, default = DEFAULT_DRY_RUN).ask() == true
        }
    }

    private suspend fun detectKotlinFiles(inputPath: Path): List<Path> {
        terminal.println(brightCyan("Scanning for Kotlin files..."))
        return MigrationManager.findKtFiles(inputPath.pathString)
    }

    private fun confirmDetectedFiles(files: List<Path>): Boolean {
        terminal.println(green("Detected ${files.size} Kotlin files:"))

        // Show first few files as preview
        files.take(5).forEach { file ->
            terminal.println(brightBlue("   • .../${file.parent.fileName}/${file.fileName}"))
        }

        if (files.size > 5) {
            terminal.println(brightBlue("   ... and ${files.size - 5} more files"))
        }

        return if (files.isEmpty()) {
            terminal.println(red("No Kotlin files found in the specified directory"))
            false
        } else {
            YesNoPrompt("\nContinue with these files?", terminal, default = true).ask() == true
        }
    }

    private suspend fun runMigration(
        ktFiles: List<Path>,
        inputDirectory: String,
        projectName: String,
        sharedModuleName: String,
        customPreview: String,
        isDryRun: Boolean,
        outputDirectory: String
    ): Pair<List<MigrationFileRow>, MigrationSummary> =
        ktFiles.map { path ->
            MigrationManager.processFile(
                filePath = path,
                configs = MigrationConfigs(
                    kmpProject = projectName,
                    sharedModule = sharedModuleName,
                    inputPath = inputDirectory,
                    customPreview = customPreview.ifEmpty { null },
                    outputDir = outputDirectory.ifEmpty { null },
                    dryRun = isDryRun,
                )
            )
        }.let { list ->
            list to MigrationSummary(
                totalFiles = list.size,
                totalChanged = list.count { (_, hasChanged, _) -> hasChanged },
                totalChanges = list.sumOf { (_, _, changes) -> changes }
            )
        }

}

suspend fun main(args: Array<String>) = KMPifyCli().main(args)