package mahmoud.habib.kmpify.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mahmoud.habib.kmpify.model.MigrationFileRow
import mahmoud.habib.kmpify.model.MigrationSummary
import mahmoud.habib.kmpify.utils.MigrationManager
import mahmoud.habib.kmpify.utils.sumOf

class MigrationWindowViewmodel : ViewModel() {

    // State variables
    private val _inputDirectory = MutableStateFlow("")
    val inputDirectory: StateFlow<String> = _inputDirectory.asStateFlow()

    private val _outputDirectory = MutableStateFlow("")
    val outputDirectory: StateFlow<String> = _outputDirectory.asStateFlow()

    private val _projectName = MutableStateFlow("")
    val projectName: StateFlow<String> = _projectName.asStateFlow()

    private val _sharedModuleName = MutableStateFlow("")
    val sharedModuleName: StateFlow<String> = _sharedModuleName.asStateFlow()

    private val _customPreview = MutableStateFlow("")
    val customPreview: StateFlow<String> = _customPreview.asStateFlow()

    private val _isDryRun = MutableStateFlow(false)
    val isDryRun: StateFlow<Boolean> = _isDryRun.asStateFlow()

    private val _report = MutableStateFlow<MigrationSummary?>(null)
    val report: StateFlow<MigrationSummary?> = _report.asStateFlow()


    private val _migrationFiles : MutableList<MigrationFileRow> = mutableListOf()
    val migrationFiles: List<MigrationFileRow> = _migrationFiles



    // Update functions
    fun updateInputDirectory(path: String) {
        _inputDirectory.update { path }
    }

    fun updateOutputDirectory(path: String) {
        _outputDirectory.update { path }
    }

    fun updateProjectName(name: String) {
        _projectName.update { name }
    }

    fun updateSharedModuleName(name: String) {
        _sharedModuleName.update { name }
    }

    fun updateCustomPreview(preview: String) {
        _customPreview.update { preview }
    }

    fun toggleDryRun(checked: Boolean) {
        _isDryRun.update { checked }
    }

    fun onClickRunMigration() {
        viewModelScope.launch {
            val (migrationFiles, summary) = runMigration(
                inputDirectory = inputDirectory.value,
                projectName = projectName.value,
                sharedModuleName = sharedModuleName.value,
                customPreview = customPreview.value,
                isDryRun = isDryRun.value,
                outputDirectory = outputDirectory.value
            )
            _migrationFiles.clear()
            _migrationFiles.addAll(migrationFiles)
            _report.update { summary }
        }
    }

    private suspend fun runMigration(
        inputDirectory: String,
        projectName: String,
        sharedModuleName: String,
        customPreview: String,
        isDryRun: Boolean,
        outputDirectory: String
    ): Pair<List<MigrationFileRow>,MigrationSummary> = MigrationManager.findKtFiles(inputDirectory)
        .map { path ->
            MigrationManager.processFile(
                filePath = path,
                kmpProject = projectName,
                sharedModule = sharedModuleName.ifEmpty { DEFAULT_SHARED_MODULE_NAME },
                inputPath = inputDirectory,
                customPreview = customPreview.ifEmpty { null },
                outputDir = outputDirectory.ifEmpty { null },
                dryRun = isDryRun,
            )
        }.let { list ->
            list to
            MigrationSummary(
                totalFiles = list.size,
                totalChanged = list.count { (_, hasChanged, _) -> hasChanged },
                totalChanges = list.sumOf { (_, _, changes) -> changes }
            )
        }

}