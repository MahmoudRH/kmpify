package mahmoud.habib.kmpify.utils

import com.github.ajalt.mordant.rendering.TextColors
import mahmoud.habib.kmpify.model.MigrationFileRow
import mahmoud.habib.kmpify.model.MigrationSummary


fun printMigrationSummary(report: MigrationSummary) {
    // Define the box width (adjust as needed)
    val boxWidth = 50

    // Top border with title
    val title = "< Migration Summary >"
    val border = "=".repeat((boxWidth - title.length) / 2)
    val topBorder = "+$border$title$border+"
    println(TextColors.green(topBorder))

    // Summary content
    val content = listOf(
        "Total .kt Files: ${report.totalFiles}",
        "Files Changed: ${report.totalChanged}",
        "Drawable Resource Replaced: ${report.totalChanges.painterResource}",
        "String Resource Replaced: ${report.totalChanges.stringResource}",
        "R Imports Replaced: ${report.totalChanges.rImport}",
        "Individual Resource Imports Added: ${report.totalChanges.resourceImports}"
    )

    // Print each line inside the box
    content.forEach { line ->
        println(TextColors.green("| ${line.padEnd(boxWidth - 3)} |"))
    }

    // Bottom border
    println(TextColors.green("""+${"=".repeat(boxWidth-1)}+"""))
}

fun printAsTree(files: List<MigrationFileRow>) {
    if (files.isEmpty()) return

    // Build a tree structure from the paths
    val tree = buildTree(files)

    // Print the tree starting from root level
    printTree(tree, "", true)
}

private fun buildTree(rows: List<MigrationFileRow>): Map<String, Any> {
    val tree = mutableMapOf<String, Any>()

    rows.forEach { row ->
        var current = tree
        val parts = row.filePath.split("/", "\\").filter { it.isNotEmpty() }

        parts.forEachIndexed { index, part ->
            if (index == parts.lastIndex) {
                // This is a file (leaf node)
                current[part] = if (row.hasChanged) "CHANGED" else "UNCHANGED"
            } else {
                // This is a directory
                if (current[part] == null) {
                    current[part] = mutableMapOf<String, Any>()
                }
                current = current[part] as MutableMap<String, Any>
            }
        }
    }

    return tree
}

private fun printTree(node: Map<String, Any>, prefix: String, isRoot: Boolean) {
    val entries = node.entries.sortedBy { it.key }

    entries.forEachIndexed { index, (name, value) ->
        val isLast = index == entries.size - 1
        val currentPrefix = if (isRoot) "" else prefix
        val connector = if (isRoot) "" else if (isLast) "└── " else "├── "

        if (value == "CHANGED") {
            println(TextColors.green("$currentPrefix$connector$name"))
        }else{
            println(TextColors.gray("$currentPrefix$connector$name"))
        }

        if (value is Map<*, *>) {
            val nextPrefix = if (isRoot) "" else prefix + if (isLast) "    " else "│   "
            printTree(value as Map<String, Any>, nextPrefix, false)
        }
    }
}
