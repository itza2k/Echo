package org.itza2k.echo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.itza2k.echo.data.model.EkoReflection
import org.itza2k.echo.data.model.EkoStatus
import org.itza2k.echo.ui.theme.LocalResonanceColors

/**
 * A card that displays the current focus of Echo, including the current task,
 * a timer button, and a "Peek" button for Claude narrative.
 */
@Composable
fun EchoFocusCard(
    ekoStatus: EkoStatus?,
    ekoReflection: EkoReflection?,
    isOnline: Boolean,
    onTimerClick: () -> Unit,
    onPeekClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalResonanceColors.current

    // Timer state
    var isTimerRunning by remember { mutableStateOf(false) }

    // Narrative state
    var isNarrativeVisible by remember { mutableStateOf(false) }

    // Animation for the timer progress
    val infiniteTransition = rememberInfiniteTransition(label = "timerTransition")
    val timerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "timerProgressAnimation"
    )

    // Card with subtle border animation when timer is running
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = colors.surface,
        border = if (isTimerRunning) {
            BorderStroke(
                width = 2.dp,
                color = colors.primary
            )
        } else {
            BorderStroke(
                width = 1.dp,
                color = colors.onSurface.copy(alpha = 0.12f)
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with title and peek button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Echo's Focus Now",
                    style = androidx.compose.material.MaterialTheme.typography.h6,
                    color = colors.onSurface
                )

                // Peek button (Claude Narrative)
                TextButton(
                    onClick = { 
                        if (isOnline) {
                            isNarrativeVisible = !isNarrativeVisible
                            onPeekClick()
                        }
                    },
                    enabled = isOnline
                ) {
                    Text(
                        text = if (isNarrativeVisible) "Hide" else "Peek",
                        color = if (isOnline) colors.primary else colors.onSurface.copy(alpha = 0.38f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current task display
            Text(
                text = ekoStatus?.currentTask?.title ?: "No task scheduled",
                style = androidx.compose.material.MaterialTheme.typography.h5,
                color = colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Task description (if available)
            ekoStatus?.currentTask?.description?.let { description ->
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        style = androidx.compose.material.MaterialTheme.typography.body1,
                        color = colors.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Timer button with circular progress
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circular progress indicator
                if (isTimerRunning) {
                    CircularProgressIndicator(
                        progress = timerProgress,
                        modifier = Modifier.size(64.dp),
                        color = colors.primary,
                        strokeWidth = 4.dp
                    )
                }

                // Play/Pause button
                Button(
                    onClick = { 
                        isTimerRunning = !isTimerRunning
                        onTimerClick()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (isTimerRunning) "⏸" else "▶",
                        color = colors.onPrimary,
                        style = MaterialTheme.typography.h6
                    )
                }
            }

            // Claude narrative (if visible and available)
            AnimatedVisibility(
                visible = isNarrativeVisible && ekoReflection != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Echo's Narrative:",
                        style = androidx.compose.material.MaterialTheme.typography.subtitle1,
                        color = colors.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = ekoReflection?.message ?: "No narrative available",
                        style = androidx.compose.material.MaterialTheme.typography.body1,
                        color = colors.onSurface.copy(alpha = 0.87f)
                    )
                }
            }
        }
    }
}
