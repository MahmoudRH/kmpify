package mahmoud.habib.kmpify.core

enum class KmpImport(val import:String) {
    JETBRAINS_PREVIEW("org.jetbrains.compose.ui.tooling.preview.Preview"),
    JETBRAINS_FONT("org.jetbrains.compose.resources.Font"),
    JETBRAINS_PAINTER_RESOURCE("org.jetbrains.compose.resources.painterResource"),
    JETBRAINS_STRING_RESOURCE("org.jetbrains.compose.resources.stringResource"),
    JETBRAINS_DRAWABLE_RESOURCE("org.jetbrains.compose.resources.DrawableResource"),
    JETBRAINS_STRING_RESOURCE_TYPE("org.jetbrains.compose.resources.StringResource"),
    KOIN_COMPOSE_VIEWMODEL("org.koin.compose.viewmodel.koinViewModel"),
}