package mahmoud.habib.kmpify.ui.screens.detailed_report

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.lifecycle.viewmodel.compose.viewModel
import mahmoud.habib.kmpify.model.MigrationFileRow
import mahmoud.habib.kmpify.ui.screens.MigrationWindowViewmodel
import mahmoud.habib.kmpify.ui.theme.MigratorTheme

@Composable
fun DetailedReportWindow(
    viewmodel: MigrationWindowViewmodel = viewModel(),
    onCloseRequest: () -> Unit
) {
    val data = remember { mutableStateListOf<MigrationFileRow>().apply { addAll(viewmodel.migrationFiles) } }
    Window(
        onCloseRequest = onCloseRequest,
        title = "Migration Report",
    ) {
        MigratorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Title row (sticky-style, not scrollable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(.25f).padding(vertical = 8.dp),
                            text = "No.",
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center,
                        )
                        listOf(
                            "File Name",
                            "Changed",
                            "R Import",
                            "Res Imports",
                            "Drawable",
                            "Strings",
                            "Imports Added"
                        ).forEachIndexed { i, title ->
                            Text(
                                text = title,
                                modifier = Modifier
                                    .weight(if (i == 0) 1.25f else if (i == 6) 1.75f else 0.75f)
                                    .clickable {
                                            when (title) {
                                                "File Name" -> { data.sortBy { it.filePath.substringAfterLast('/') } }
                                                "Changed" -> { data.sortByDescending { it.hasChanged } }
                                                "R Import" -> { data.sortByDescending { it.changes.rImport } }
                                                "Res Imports" -> { data.sortByDescending { it.changes.resourceImports } }
                                                "Drawable" -> { data.sortByDescending { it.changes.painterResource } }
                                                "Strings" -> { data.sortByDescending { it.changes.stringResource } }
                                                "Imports Added" -> { data.sortByDescending { it.changes.importsAdded.size } }
                                        }
                                    }
                                    .padding(vertical = 8.dp)
                                ,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    LazyColumn {
                        itemsIndexed(data) { index, it ->
                            var isExpanded by remember { mutableStateOf(false) }
                            ReportRow(
                                index = index + 1,
                                row = it,
                                modifier = Modifier
                                    .background(
                                        color = if (it.hasChanged) Color.Green.copy(alpha = 0.05f)
                                        else Color.LightGray.copy(alpha = 0.1f)
                                    )
                                    .then(
                                        if (it.hasChanged) Modifier.clickable { isExpanded = !isExpanded }
                                    else Modifier
                                    )
                                    .padding(horizontal = 16.dp),
                                isExpanded = isExpanded
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportRow(index: Int, row: MigrationFileRow, modifier: Modifier = Modifier, isExpanded: Boolean) {
    Row(modifier.animateContentSize(), verticalAlignment = Alignment.CenterVertically) {
        RowCell(text = index.toString(), weight = .25f)
        RowCell(text = row.filePath.substringAfterLast('/'), weight = 1.25f)
        RowCell(text = row.hasChanged.toText())
        RowCell(text = row.changes.rImport.takeIf { it > 0 }?.let { "Replaced" } ?: "-")
        RowCell(text = row.changes.resourceImports.toString())
        RowCell(text = row.changes.painterResource.toString())
        RowCell(text = row.changes.stringResource.toString())
        if (isExpanded) {
            RowCell(
                text = row.changes.importsAdded.joinToString("\n") { it.import },
                fontSize = 11.sp,
                weight = 1.75f,
                maxLines = Int.MAX_VALUE
            )
        }else {
            RowCell(text = row.changes.importsAdded.size.toString(), fontSize = 11.sp, weight = 1.75f)
        }

    }
}

@Composable
private fun RowScope.RowCell(text: String, weight: Float = 0.75f, fontSize: TextUnit = 14.sp, maxLines: Int = 1) {
    Text(
        modifier = Modifier.weight(weight),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light,
        text = text.takeIf { text != "0" } ?: "-",
        fontSize = fontSize
    )
}

private fun Boolean.toText() = if (this) "Yes ✅" else "No"