package mahmoud.habib.kmpify.model

data class MigrationSummary(
    val totalFiles: Int,
    val totalChanged: Int,
    val totalChanges: ProcessingChanges
)