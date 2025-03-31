package org.itza2k.echo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.itza2k.echo.ui.theme.LocalIsDarkTheme
import org.itza2k.echo.ui.theme.LocalResonanceColors
import org.itza2k.echo.ui.theme.ThemeMode

/**
 * A toggle component for switching between light, dark, and system themes.
 */
@Composable
fun ThemeToggle(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalResonanceColors.current
    val isDarkTheme = LocalIsDarkTheme.current

    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Light theme option
        ThemeOption(
            selected = currentTheme == ThemeMode.LIGHT,
            onClick = { onThemeChange(ThemeMode.LIGHT) },
            icon = "☀️",
            label = "Light",
            tint = if (isDarkTheme) colors.onSurface.copy(alpha = 0.7f) else colors.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        // System theme option
        ThemeOption(
            selected = currentTheme == ThemeMode.SYSTEM,
            onClick = { onThemeChange(ThemeMode.SYSTEM) },
            icon = "⚙️",
            label = "System",
            tint = colors.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Dark theme option
        ThemeOption(
            selected = currentTheme == ThemeMode.DARK,
            onClick = { onThemeChange(ThemeMode.DARK) },
            icon = "🌙",
            label = "Dark",
            tint = if (isDarkTheme) colors.primary else colors.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * A selectable theme option with an icon and label.
 */
@Composable
private fun ThemeOption(
    selected: Boolean,
    onClick: () -> Unit,
    icon: String,
    label: String,
    tint: Color
) {
    val colors = LocalResonanceColors.current

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "scaleAnimation"
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) colors.primary else colors.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 200),
        label = "colorAnimation"
    )

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 20.sp,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Label
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) colors.primary else colors.onSurface.copy(alpha = 0.7f)
            )

            // Selection indicator
            if (selected) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
