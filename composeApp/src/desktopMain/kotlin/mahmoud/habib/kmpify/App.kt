package mahmoud.habib.kmpify

import androidx.compose.runtime.Composable
import mahmoud.habib.kmpify.ui.screens.MigratorWindow
import mahmoud.habib.kmpify.ui.theme.MigratorTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MigratorTheme {
        MigratorWindow()
    }
}