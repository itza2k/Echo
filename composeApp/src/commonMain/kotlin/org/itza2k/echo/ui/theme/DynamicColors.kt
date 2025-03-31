package org.itza2k.echo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Gets the dynamic color scheme based on the device's wallpaper if available.
 * Falls back to the provided default color scheme if dynamic colors are not available.
 *
 * @param isDarkTheme Whether to use dark theme colors
 * @param defaultColorScheme The default color scheme to use if dynamic colors are not available
 * @return The dynamic color scheme if available, otherwise the default color scheme
 */
@Composable
expect fun getDynamicColorScheme(isDarkTheme: Boolean, defaultColorScheme: ColorScheme): ColorScheme