package org.itza2k.echo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Desktop implementation of getDynamicColorScheme.
 * Desktop doesn't support dynamic colors based on wallpaper,
 * so this implementation simply returns the default color scheme.
 */
@Composable
actual fun getDynamicColorScheme(isDarkTheme: Boolean, defaultColorScheme: ColorScheme): ColorScheme {
    // Desktop doesn't support dynamic colors, so just return the default color scheme
    return defaultColorScheme
}