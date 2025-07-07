package mahmoud.habib.kmpify.model

data class MigrationFileRow(
    val fileName: String,
    val hasChanged: Boolean,
    val changes: ProcessingChanges
)