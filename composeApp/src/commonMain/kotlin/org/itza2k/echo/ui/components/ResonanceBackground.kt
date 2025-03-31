package org.itza2k.echo.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.ui.theme.LocalIsDarkTheme
import org.itza2k.echo.ui.theme.ResonanceColors

// Animated background that changes colors based on time of day
@Composable
fun ResonanceBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Get current hour for color selection
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = now.hour
    val isDarkTheme = LocalIsDarkTheme.current
    val (primaryColor, secondaryColor, tertiaryColor) = remember(hour, isDarkTheme) {
        when {
            // Morning (6-11): Warmer tones
            hour in 6..11 -> {
                if (isDarkTheme) {
                    Triple(
                        Color(0xFF3F2D6D), // Dark purple
                        Color(0xFF2D1F4D), // Darker purple
                        Color(0xFF1A1238)  // Very dark purple
                    )
                } else {
                    Triple(
                        Color(0xFFF8E3CB), // Light peach
                        Color(0xFFF5D6B9), // Peach
                        Color(0xFFEEC4A2)  // Darker peach
                    )
                }
            }
            // Midday (12-17): Cooler tones
            hour in 12..17 -> {
                if (isDarkTheme) {
                    Triple(
                        Color(0xFF1F3A5F), // Dark blue
                        Color(0xFF162C48), // Darker blue
                        Color(0xFF0E1C30)  // Very dark blue
                    )
                } else {
                    Triple(
                        Color(0xFFE6F4F1), // Light blue-white
                        Color(0xFFD6EBE9), // Light blue
                        Color(0xFFC2E0DE)  // Slightly darker blue
                    )
                }
            }
            // Evening (18-21): Warmer tones again
            hour in 18..21 -> {
                if (isDarkTheme) {
                    Triple(
                        Color(0xFF4D2D2D), // Dark red-brown
                        Color(0xFF3A2222), // Darker red-brown
                        Color(0xFF291818)  // Very dark red-brown
                    )
                } else {
                    Triple(
                        Color(0xFFF9E0D9), // Light pink
                        Color(0xFFF5D0C6), // Pink
                        Color(0xFFEEBFB2)  // Darker pink
                    )
                }
            }
            // Night (22-5): Deep tones
            else -> {
                if (isDarkTheme) {
                    Triple(
                        Color(0xFF1A1A2E), // Dark navy
                        Color(0xFF121224), // Darker navy
                        Color(0xFF0A0A1A)  // Very dark navy
                    )
                } else {
                    Triple(
                        Color(0xFFE0E0E8), // Light gray-blue
                        Color(0xFFD0D0DC), // Gray-blue
                        Color(0xFFC0C0D0)  // Darker gray-blue
                    )
                }
            }
        }
    }

    // Animation setup
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundTransition")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAnimation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnimation"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = maxOf(canvasWidth, canvasHeight) * scale

            rotate(rotation) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor, secondaryColor, tertiaryColor),
                        center = Offset(canvasWidth / 2, canvasHeight / 2),
                        radius = radius
                    ),
                    center = Offset(canvasWidth / 2, canvasHeight / 2),
                    radius = radius
                )
            }
        }
        content()
    }
}
