package mahmoud.habib.kmpify.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mahmoud.habib.kmpify.ui.components.LabeledRow
import mahmoud.habib.kmpify.ui.components.MigrationSummaryCard
import mahmoud.habib.kmpify.ui.screens.detailed_report.DetailedReportWindow
import mahmoud.habib.kmpify.utils.browseForDirectoryOrKtFile

const val DEFAULT_SHARED_MODULE_NAME = "composeapp"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigratorWindow(
    viewmodel: MigrationWindowViewmodel = viewModel()
) {
    val inputDirectory by viewmodel.inputDirectory.collectAsState()
    val outputDirectory by viewmodel.outputDirectory.collectAsState()
    val projectName by viewmodel.projectName.collectAsState()
    val sharedModuleName by viewmodel.sharedModuleName.collectAsState()
    val customPreview by viewmodel.customPreview.collectAsState()
    val isDryRun by viewmodel.isDryRun.collectAsState()
    val report by viewmodel.report.collectAsState()
    var openReportWindow by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = "Compose Multiplatform Migrator",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AnimatedVisibility(visible = report != null) {
                report?.let {
                    MigrationSummaryCard(report = it, onClickViewFullReport = { openReportWindow = true})
                }
            }

            // Input Directory
            LabeledRow(
                label = "Input Path",
                placeholder = "e.g. /app/src/main/java/... (Android source folder)",
                value = inputDirectory,
                onValueChange = { viewmodel.updateInputDirectory(it) },
                onBrowseClick = {
                    browseForDirectoryOrKtFile("Select Input Directory")?.let {
                        viewmodel.updateInputDirectory(it)
                    }
                }
            )

            // Output Directory
            LabeledRow(
                label = "Output Path",
                placeholder = "e.g. /composeApp/src/commonMain/kotlin/... (KMP shared folder)",
                value = outputDirectory,
                onValueChange = { viewmodel.updateOutputDirectory(it) },
                onBrowseClick = {
                    browseForDirectoryOrKtFile("Select Output Directory")?.let {
                        viewmodel.updateOutputDirectory(it)
                    }
                }
            )

            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Project Name
                LabeledRow(
                    modifier = Modifier.weight(1f),
                    label = "KMP Project Name",
                    value = projectName,
                    onValueChange = { viewmodel.updateProjectName(it) },
                )

                // Shared Module Name
                LabeledRow(
                    modifier = Modifier.weight(1f),
                    label = "Shared Module Name",
                    value = sharedModuleName,
                    onValueChange = { viewmodel.updateSharedModuleName(it) },
                    supportingText = "will default to '$DEFAULT_SHARED_MODULE_NAME' if left empty"
                )
                // Custom Preview
                LabeledRow(
                    modifier = Modifier.weight(1f),
                    label = "Custom Preview",
                    value = customPreview,
                    onValueChange = { viewmodel.updateCustomPreview(it) },
                    supportingText = "(if any)"
                )
            }

            HorizontalDivider()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isDryRun,
                    onCheckedChange = { viewmodel.toggleDryRun(it) })
                Column {
                    Text(
                        text = "Dry Run?",
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Dry Run mode runs the migration but doesn't write anything on disk.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                    )
                }
            }

            // Run Button
            Button(
                onClick = { viewmodel.onClickRunMigration() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = inputDirectory.isNotEmpty() && projectName.isNotEmpty(),
            ) {
                Text(
                    text = "Run Migration",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (openReportWindow) {
        DetailedReportWindow(
            onCloseRequest = { openReportWindow = false },
        )
    }
}


