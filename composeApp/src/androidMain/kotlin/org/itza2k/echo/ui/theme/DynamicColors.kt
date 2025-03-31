package org.itza2k.echo.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation of getDynamicColorScheme.
 * Uses Material You dynamic colors on Android 12+ devices.
 */
@Composable
actual fun getDynamicColorScheme(isDarkTheme: Boolean, defaultColorScheme: ColorScheme): ColorScheme {
    // Dynamic colors are only available on Android 12+
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isDarkTheme) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        // Fall back to the default color scheme
        defaultColorScheme
    }
}