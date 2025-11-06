package mahmoud.habib.kmpify.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mahmoud.habib.kmpify.model.MigrationFileRow
import mahmoud.habib.kmpify.model.ProcessingChanges
import mahmoud.habib.kmpify.model.ResourceReferences
import mahmoud.habib.mahmoud.habib.kmpify.model.MigrationConfigs
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.collections.iterator
import kotlin.io.path.*
import kotlin.text.get

object MigrationManager {
    private const val DRAWABLE = "drawable"
    private const val STRING = "string"
    private const val FONT = "font"

    suspend fun findKtFiles(inputPath: String): List<Path> {
        return withContext(Dispatchers.IO) {

            val path = Paths.get(inputPath)
            val ktFiles: List<Path> = when {
                Files.isRegularFile(path) && path.toString().endsWith(".kt") -> listOf(path)
                Files.isDirectory(path) -> Files.walk(path)
                    .filter { it.toString().endsWith(".kt") }
                    .toList()

                else -> emptyList()
            }

            if (ktFiles.isEmpty()) {
                return@withContext emptyList()
            }

            return@withContext ktFiles
        }

    }

    /**
     * Process a single .kt file and apply transformations.
     */
    suspend fun processFile(
        filePath: Path,
        configs: MigrationConfigs,
    ): MigrationFileRow {
        return withContext(Dispatchers.Default) {
            with(configs) {
                val changes = ProcessingChanges()
                val originalContent = filePath.readText()
                var content = originalContent

                val baseResPath = "$kmpProject.$sharedModule.generated.resources"

                // Replace R class imports
                content = replaceRClassImports(content, baseResPath, changes)

                // Replace resource imports
                content = replaceResourceImports(content)

                content = replacePreviewImports(content)
                // Replace custom preview imports
                if (customPreview?.isNotBlank() == true) {
                    content = replaceCustomPreview(content, customPreview)
                }

                content = replaceKoinViewmodelImport(content)

                // Replace preview annotations
                if (removePreviewParameters)
                    content = replacePreviewAnnotations(content)

                // Replace annotation parameters
                content = replaceAnnotationParameters(content)

                // Process resource references
                val resourceRefs = findAndReplaceResourceReferences(content, changes)
                content = resourceRefs.first
                val refs = resourceRefs.second

                // Replace id with resource in compose functions
                content = replaceIdWithResource(content)

                // Rebuild file with proper import ordering
                content = rebuildFileWithImports(content, refs, baseResPath, changes)

                //count added imports
                changes.importsAdded = countAddedImports(content)

                val filename = filePath.pathString
                if (dryRun) {
                    return@withContext MigrationFileRow(filename, originalContent != content, changes)
                }

                // Write output file
                writeOutputFile(filePath, content, inputPath, outputDir)

                return@withContext MigrationFileRow(filename, originalContent != content, changes)
            }
        }
    }

    private fun countAddedImports(content: String): MutableSet<KmpImport> {
        val importPattern = Regex("""^import\s+([\w.]+)""", RegexOption.MULTILINE)
        val existingImports = importPattern.findAll(content).map { it.groups[1]?.value ?: "" }.toSet()

        val importsAdded = mutableSetOf<KmpImport>()
        KmpImport.entries.forEach {
            if(existingImports.contains(it.import)){
                importsAdded.add(it)
            }
        }
        return importsAdded
    }

    /**
     * Replace R class imports with Res imports
     */
    private fun replaceRClassImports(
        content: String,
        baseResPath: String,
        changes: ProcessingChanges
    ): String {
        val rImportPattern = Regex("""import\s+([\w.]+\.R)\b""")
        val result = rImportPattern.replace(content) { "import $baseResPath.Res" }
        changes.rImport = rImportPattern.findAll(content).count()
        return result
    }

    /**
     * Replace various resource-related imports
     */
    private fun replaceResourceImports(content: String): String {
        return content
            .replaceWord(
                "androidx.compose.ui.res.painterResource",
                KmpImport.JETBRAINS_PAINTER_RESOURCE.import
            )
            .replaceWord(
                "androidx.compose.ui.res.stringResource",
                KmpImport.JETBRAINS_STRING_RESOURCE.import
            )
            .replaceWord(
                "androidx.annotation.DrawableRes",
                KmpImport.JETBRAINS_DRAWABLE_RESOURCE.import
            )
            .replaceWord(
                "androidx.annotation.StringRes",
                KmpImport.JETBRAINS_STRING_RESOURCE_TYPE.import
            )
            .replaceWord(
                "androidx.compose.ui.text.font.Font",
                KmpImport.JETBRAINS_FONT.import
            )
    }

    /**
     * Replace preview imports
     */
    private fun replacePreviewImports(content: String): String {
        return content
            .replaceWord(
                "androidx.compose.ui.tooling.preview.Preview",
                KmpImport.JETBRAINS_PREVIEW.import
            ).replaceWord(
                "androidx.compose.ui.tooling.preview.PreviewParameter",
                KmpImport.JETBRAINS_PREVIEW_PARAMETER.import,
            ).replaceWord(
                "androidx.compose.ui.tooling.preview.PreviewParameterProvider",
                KmpImport.JETBRAINS_PREVIEW_PARAMETER_PROVIDER.import,
            )
    }

    /**
     * Replace custom preview imports
     */
    private fun replaceCustomPreview(content: String, customPreview: String): String {
        val customPreviewImportPattern = Regex("""import\s+([\w.]+\.$customPreview)\b""")
        return customPreviewImportPattern.replace(content) {
            "import ${KmpImport.JETBRAINS_PREVIEW.import}"
        }.replace(customPreview, "Preview")
    }

    /**
     * Replace koin.androidx viewmodel import with koin.compose viewmodel import
     */
    private fun replaceKoinViewmodelImport(content: String): String {
        return content.replaceWord(
            "org.koin.androidx.compose.koinViewModel",
            KmpImport.KOIN_COMPOSE_VIEWMODEL.import
        )
    }

    /**
     * Replace preview annotations with parameters to simple @Preview
     */
    private fun replacePreviewAnnotations(content: String): String {
        val pattern = Regex("""@Preview\([^)]*?\)""")
        return pattern.replace(content, "@Preview")
    }

    /**
     * Replace annotation parameters (@DrawableRes, @StringRes)
     */
    private fun replaceAnnotationParameters(content: String): String {
        val annotationPatterns = mapOf(
            "DrawableRes" to "DrawableResource",
            "StringRes" to "StringResource"
        )

        var result = content
        for ((annotation, newType) in annotationPatterns) {
            val pattern = Regex("""@$annotation\s+([\w<>]+\s+)?([\w_]+)\s*:\s*Int""")
            result = pattern.replace(result) { matchResult ->
                val prefix = matchResult.groups[1]?.value ?: ""
                val varName = matchResult.groups[2]?.value ?: ""
                "$prefix$varName: $newType"
            }
        }
        return result
    }

    /**
     * Find and replace resource references (R.drawable, R.string, R.font)
     */
    private fun findAndReplaceResourceReferences(
        content: String,
        changes: ProcessingChanges
    ): Pair<String, ResourceReferences> {
        val resourceRefs = ResourceReferences()
        var result = content

        val resourceTypes = listOf(DRAWABLE, STRING, FONT)

        for (resType in resourceTypes) {
            val pattern = Regex("""R\.$resType\.(?<name>[\w_]+)\b""")
            val matches = pattern.findAll(result).toList()

            for (match in matches) {
                val name = match.groups["name"]?.value ?: continue
                result = result.replace("R.$resType.$name", "Res.$resType.$name")

                when (resType) {
                    DRAWABLE -> {
                        resourceRefs.drawable.add(name)
                        changes.painterResource++
                    }

                    STRING -> {
                        resourceRefs.string.add(name)
                        changes.stringResource++
                    }

                    FONT -> {
                        resourceRefs.font.add(name)
                    }
                }
            }
        }

        return Pair(result, resourceRefs)
    }

    /**
     * Replace 'id =' with 'resource =' in compose functions
     */
    private fun replaceIdWithResource(content: String): String {
        val functionNames = listOf("painterResource", "stringResource", "Font")
        var result = content

        for (funcName in functionNames) {
            val pattern = Regex("""($funcName)\s*\((.*?)\)""", RegexOption.DOT_MATCHES_ALL)
            result = pattern.replace(result) { matchResult ->
                val function = matchResult.groups[1]?.value ?: ""
                val args = matchResult.groups[2]?.value ?: ""
                val updatedArgs = args.replace(Regex("""\bid\s*=\s*"""), "resource = ")
                "$function($updatedArgs)"
            }
        }

        return result
    }

    /**
     * Rebuild file content with proper import ordering
     */
    private fun rebuildFileWithImports(
        content: String,
        resourceRefs: ResourceReferences,
        baseResPath: String,
        changes: ProcessingChanges
    ): String {
        // Extract existing imports
        val importPattern = Regex("""^import\s+([\w.]+)""", RegexOption.MULTILINE)
        val existingImports = importPattern.findAll(content).map { it.groups[1]?.value ?: "" }.toSet()

        if (existingImports.isEmpty() || resourceRefs.isEmpty()) {
            return content
        }

        // Generate new resource imports
        val newResourceImports = mutableSetOf<String>()
        for (resType in listOf(DRAWABLE, STRING, FONT)) {
            val resources = when (resType) {
                DRAWABLE -> resourceRefs.drawable
                STRING -> resourceRefs.string
                FONT -> resourceRefs.font
                else -> emptySet()
            }

            for (resource in resources) {
                newResourceImports.add("$baseResPath.$resource")
            }
        }

        val linesPrecedingImports = content.substringBefore("import")

        // Remove all imports from content
        val contentWithoutImports = importPattern.replace(content.replace(linesPrecedingImports, ""), "").trimStart()
        val lines = contentWithoutImports.lines().toMutableList()

        val newContent = mutableListOf<String>()

        // Add package declaration
        if (linesPrecedingImports.isNotEmpty()) {
            newContent.add(linesPrecedingImports.trim())
            newContent.add("")
        }

        // Add all imports in sorted order
        val allImports = (existingImports + newResourceImports).sorted()
        allImports.forEach { newContent.add("import $it") }
        newContent.add("")

        // Add the rest of the content (non-package lines)
        newContent.addAll(lines)

        // Update changes count
        changes.resourceImports = newResourceImports.size

        return newContent.joinToString("\n")
    }

    /**
     * Write the processed content to output file
     */
    private suspend fun writeOutputFile(
        filePath: Path,
        content: String,
        inputPath: String,
        outputDir: String?
    ) {
        withContext(Dispatchers.IO) {
            val outputPath = if (outputDir?.isNotBlank() == true) {
                val inputPathObj = Paths.get(inputPath)
                val outputDirPath = Paths.get(outputDir)

                if (inputPathObj.isRegularFile()) {
                    outputDirPath / filePath.fileName
                } else {
                    outputDirPath / inputPathObj.relativize(filePath)
                }
            } else {
                filePath.resolveSibling(filePath.nameWithoutExtension + ".kmp.kt")
            }

            outputPath.parent?.createDirectories()
            outputPath.writeText(content)
        }
    }
}