package mahmoud.habib.mahmoud.habib.kmpify.model

data class MigrationConfigs(
    val kmpProject: String,
    val sharedModule: String,
    val inputPath: String,
    val customPreview: String? = null,
    val dryRun: Boolean = false,
    val outputDir: String? = null,
    val removePreviewParameters: Boolean = false,
)
