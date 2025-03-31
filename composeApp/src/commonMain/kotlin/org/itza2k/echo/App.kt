package org.itza2k.echo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.itza2k.echo.data.api.ApiKeyManager
import org.itza2k.echo.ui.screens.MainScreen
import org.itza2k.echo.ui.theme.ResonanceTheme
import org.itza2k.echo.ui.theme.ThemeMode

/**
 * Main entry point for the Echo application.
 * Sets up the theme and main screen with navigation.
 */
@Composable
@Preview
fun App() {
    // Remember the theme mode across recompositions
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    // Remember the dynamic colors preference
    var useDynamicColors by remember { mutableStateOf(true) }

    // Initialize API key manager
    val apiKeyManager = remember { ApiKeyManager.instance }

    // Apply the theme with the current theme mode and dynamic colors preference
    ResonanceTheme(
        themeMode = themeMode,
        useDynamicColors = useDynamicColors
    ) {
        // Pass the theme mode and change handler to the MainScreen
        MainScreen(
            themeMode = themeMode,
            apiKeyManager = apiKeyManager,
            onThemeModeChange = { newThemeMode ->
                themeMode = newThemeMode
            }
        )
    }
}
