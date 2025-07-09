package mahmoud.habib.kmpify.model

data class MigrationFileRow(
    val filePath: String,
    val hasChanged: Boolean,
    val changes: ProcessingChanges
)