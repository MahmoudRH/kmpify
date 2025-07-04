package mahmoud.habib.kmpify

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Migration Tool CMP",
    ) {
        App()
    }
}