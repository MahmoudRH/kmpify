package mahmoud.habib.kmpify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable



private val lightColorScheme = lightColorScheme(
    primary = LightBluePrimary,
    onPrimary = OnLightBluePrimary,
    primaryContainer = LightBluePrimaryContainer,
    onPrimaryContainer = OnLightBluePrimaryContainer,
    secondary = LightBlueSecondary,
    onSecondary = OnLightBlueSecondary,
    secondaryContainer = LightBlueSecondaryContainer,
    onSecondaryContainer = OnLightBlueSecondaryContainer,
    tertiary = LightBlueTertiary,
    onTertiary = OnLightBlueTertiary,
    tertiaryContainer = LightBlueTertiaryContainer,
    onTertiaryContainer = OnLightBlueTertiaryContainer,
    error = LightError,
    onError = OnLightError,
    background = LightBackground,
    onBackground = OnLightBackground,
    surface = LightSurface,
    onSurface = OnLightSurface,
    // You can define other colors like surfaceVariant, outline, etc. if needed
)

private val darkColorScheme = darkColorScheme(
    primary = DarkBluePrimary,
    onPrimary = OnDarkBluePrimary,
    primaryContainer = DarkBluePrimaryContainer,
    onPrimaryContainer = OnDarkBluePrimaryContainer,
    secondary = DarkBlueSecondary,
    onSecondary = OnDarkBlueSecondary,
    secondaryContainer = DarkBlueSecondaryContainer,
    onSecondaryContainer = OnDarkBlueSecondaryContainer,
    tertiary = DarkBlueTertiary,
    onTertiary = OnDarkBlueTertiary,
    tertiaryContainer = DarkBlueTertiaryContainer,
    onTertiaryContainer = OnDarkBlueTertiaryContainer,
    error = DarkError,
    onError = OnDarkError,
    background = DarkBackground,
    onBackground = OnDarkBackground,
    surface = DarkSurface,
    onSurface = OnDarkSurface,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MigratorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        typography = Typography,
    ) {
        content()
    }
}