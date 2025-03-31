package org.itza2k.echo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define the Resonance color palette
object ResonanceColors {
    // Light Theme Colors
    val LightPrimary = Color(0xFF6750A4)
    val LightPrimaryVariant = Color(0xFF4F378B)
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightSecondary = Color(0xFF625B71)
    val LightSecondaryVariant = Color(0xFF4A4458)
    val LightOnSecondary = Color(0xFFFFFFFF)
    val LightBackground = Color(0xFFFFFBFE)
    val LightOnBackground = Color(0xFF1C1B1F)
    val LightSurface = Color(0xFFFFFBFE)
    val LightOnSurface = Color(0xFF1C1B1F)
    val LightError = Color(0xFFB3261E)
    val LightOnError = Color(0xFFFFFFFF)

    // Dark Theme Colors
    val DarkPrimary = Color(0xFFD0BCFF)
    val DarkPrimaryVariant = Color(0xFF4F378B)
    val DarkOnPrimary = Color(0xFF381E72)
    val DarkSecondary = Color(0xFFCCC2DC)
    val DarkSecondaryVariant = Color(0xFF4A4458)
    val DarkOnSecondary = Color(0xFF332D41)
    val DarkBackground = Color(0xFF1C1B1F)
    val DarkOnBackground = Color(0xFFE6E1E5)
    val DarkSurface = Color(0xFF1C1B1F)
    val DarkOnSurface = Color(0xFFE6E1E5)
    val DarkError = Color(0xFFF2B8B5)
    val DarkOnError = Color(0xFF601410)
}

// Define the light and dark color schemes for Material 3
private val LightColorScheme = lightColorScheme(
    primary = ResonanceColors.LightPrimary,
    onPrimary = ResonanceColors.LightOnPrimary,
    primaryContainer = ResonanceColors.LightPrimaryVariant,
    onPrimaryContainer = ResonanceColors.LightOnPrimary,
    secondary = ResonanceColors.LightSecondary,
    onSecondary = ResonanceColors.LightOnSecondary,
    secondaryContainer = ResonanceColors.LightSecondaryVariant,
    onSecondaryContainer = ResonanceColors.LightOnSecondary,
    background = ResonanceColors.LightBackground,
    onBackground = ResonanceColors.LightOnBackground,
    surface = ResonanceColors.LightSurface,
    onSurface = ResonanceColors.LightOnSurface,
    error = ResonanceColors.LightError,
    onError = ResonanceColors.LightOnError
)

private val DarkColorScheme = darkColorScheme(
    primary = ResonanceColors.DarkPrimary,
    onPrimary = ResonanceColors.DarkOnPrimary,
    primaryContainer = ResonanceColors.DarkPrimaryVariant,
    onPrimaryContainer = ResonanceColors.DarkOnPrimary,
    secondary = ResonanceColors.DarkSecondary,
    onSecondary = ResonanceColors.DarkOnSecondary,
    secondaryContainer = ResonanceColors.DarkSecondaryVariant,
    onSecondaryContainer = ResonanceColors.DarkOnSecondary,
    background = ResonanceColors.DarkBackground,
    onBackground = ResonanceColors.DarkOnBackground,
    surface = ResonanceColors.DarkSurface,
    onSurface = ResonanceColors.DarkOnSurface,
    error = ResonanceColors.DarkError,
    onError = ResonanceColors.DarkOnError
)

// Define the typography for Material 3
val ResonanceTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)

// Create a CompositionLocal for the current color scheme
val LocalResonanceColors = compositionLocalOf<ColorScheme> { 
    error("No ResonanceColors provided") 
}

/**
 * Theme mode options for the app.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

// Create a CompositionLocal for the current theme mode
val LocalIsDarkTheme = compositionLocalOf { false }

// Create a CompositionLocal for the current theme mode setting
val LocalThemeMode = compositionLocalOf { ThemeMode.SYSTEM }

// Create a CompositionLocal for dynamic color preference
val LocalUseDynamicColors = compositionLocalOf { false }

// Main theme composable
@Composable
fun ResonanceTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    useDynamicColors: Boolean = false,
    content: @Composable () -> Unit
) {
    // Determine if dark theme should be used based on the theme mode
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Get the appropriate color scheme
    // Use dynamic colors if enabled, otherwise use the default color scheme
    // Note: Dynamic colors are only available on Android 12+ and will be ignored on other platforms
    val defaultColorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = if (useDynamicColors) {
        getDynamicColorScheme(isDarkTheme, defaultColorScheme)
    } else {
        defaultColorScheme
    }

    CompositionLocalProvider(
        LocalResonanceColors provides colorScheme,
        LocalIsDarkTheme provides isDarkTheme,
        LocalThemeMode provides themeMode,
        LocalUseDynamicColors provides useDynamicColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ResonanceTypography,
            content = content
        )
    }
}
