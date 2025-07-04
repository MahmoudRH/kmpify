package mahmoud.habib.kmpify.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mahmoud.habib.kmpify.model.MigrationSummary

@Composable
fun MigrationSummaryCard(
    report: MigrationSummary,
    modifier: Modifier = Modifier,
    onClickViewFullReport : () -> Unit,) {
    var isExpanded by remember { mutableStateOf(true) }

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 270f else 180f,  // Open = 180, Closed = 90
        label = "ArrowRotation"
    )

    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Migration Summary", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onClickViewFullReport) {
                        Text("View Full Report")
                    }
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Expand/Collapse",
                            modifier = Modifier
                                .graphicsLayer { rotationZ = rotation }
                                .size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FlowRow(
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    ReportLine("Total .kt Files", report.totalFiles)
                    ReportLine("Files Changed", report.totalChanged)
                    ReportLine("Drawable Resource Replaced", report.totalChanges.painterResource)
                    ReportLine("String Resource Replaced", report.totalChanges.stringResource)
                    ReportLine("R Imports Replaced", report.totalChanges.rImport)
                    ReportLine("Individual Resource Imports Added", report.totalChanges.resourceImports)
                }
            }
        }
    }
}

@Composable
fun FlowRowScope.ReportLine(label: String, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.weight(1f).padding(vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text("$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
