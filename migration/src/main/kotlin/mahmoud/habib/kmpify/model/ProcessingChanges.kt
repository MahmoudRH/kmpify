package mahmoud.habib.kmpify.model

import mahmoud.habib.kmpify.core.KmpImport


data class ProcessingChanges(
    var rImport: Int = 0,
    var painterResource: Int = 0,
    var stringResource: Int = 0,
    var resourceImports: Int = 0,
    var importsAdded: Set<KmpImport> = emptySet(),
) {
    operator fun plus(other: ProcessingChanges): ProcessingChanges {
        return ProcessingChanges(
            rImport = rImport + other.rImport,
            painterResource = painterResource + other.painterResource,
            stringResource = stringResource + other.stringResource,
            resourceImports = resourceImports + other.resourceImports,
        )
    }
}

