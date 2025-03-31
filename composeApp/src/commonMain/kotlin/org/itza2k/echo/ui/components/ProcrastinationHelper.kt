package org.itza2k.echo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// Helps users overcome procrastination with motivation and exercises
@Composable
fun ProcrastinationHelper(
    modifier: Modifier = Modifier,
    onActionSelected: (String) -> Unit = {}
) {
    val (message, tip) = remember { getRandomMotivationAndTip() }
    val actions = remember { generateActionSuggestions() }
    var showBreathingExercise by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.widthIn(max = 400.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Focus Mode",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Eliminate Distractions",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Divider
            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )

            if (showBreathingExercise) {
                // Show breathing exercise
                BreathingExercise(
                    onComplete = { 
                        showBreathingExercise = false 
                    }
                )
            } else {
                // Motivational message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Tip
                Text(
                    text = "Tip: $tip",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Action buttons - now showing both options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pomodoro button
                    FilledTonalButton(
                        onClick = { onActionSelected(actions[0]) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(actions[0])
                    }

                    // Breathing exercise button
                    OutlinedButton(
                        onClick = { 
                            showBreathingExercise = true
                            onActionSelected(actions[1])
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(actions[1])
                    }

                    // Stretch break button
                    OutlinedButton(
                        onClick = { 
                            onActionSelected(actions[2])
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(actions[2])
                    }
                }
            }
        }
    }
}

// Simple breathing exercise to help users relax
@Composable
private fun BreathingExercise(
    onComplete: () -> Unit
) {
    var breathingPhase by remember { mutableStateOf("Get Ready") }
    var secondsLeft by remember { mutableStateOf(5) }
    var totalSecondsLeft by remember { mutableStateOf(120) } // 2 minutes

    // Animation for the breathing circle
    val scale by animateFloatAsState(
        targetValue = when (breathingPhase) {
            "Inhale" -> 1.5f
            "Hold" -> 1.5f
            "Exhale" -> 1.0f
            else -> 1.0f
        },
        animationSpec = tween(
            durationMillis = when (breathingPhase) {
                "Inhale" -> 4000
                "Hold" -> 1000
                "Exhale" -> 4000
                else -> 1000
            },
            easing = LinearEasing
        )
    )

    // Timer effect
    LaunchedEffect(key1 = Unit) {
        while (totalSecondsLeft > 0) {
            delay(1000)
            secondsLeft--
            totalSecondsLeft--

            if (secondsLeft <= 0) {
                when (breathingPhase) {
                    "Get Ready" -> {
                        breathingPhase = "Inhale"
                        secondsLeft = 4
                    }
                    "Inhale" -> {
                        breathingPhase = "Hold"
                        secondsLeft = 4
                    }
                    "Hold" -> {
                        breathingPhase = "Exhale"
                        secondsLeft = 4
                    }
                    "Exhale" -> {
                        breathingPhase = "Inhale"
                        secondsLeft = 4
                    }
                }
            }
        }
        onComplete()
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "2-Minute Breathing Exercise",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Breathing circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ) {}

            Text(
                text = breathingPhase,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        Text(
            text = when (breathingPhase) {
                "Get Ready" -> "Prepare to begin..."
                "Inhale" -> "Breathe in slowly through your nose..."
                "Hold" -> "Hold your breath..."
                "Exhale" -> "Breathe out slowly through your mouth..."
                else -> ""
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Timer
        Text(
            text = "Time remaining: ${totalSecondsLeft / 60}:${totalSecondsLeft % 60}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Skip button
        TextButton(
            onClick = onComplete
        ) {
            Text("Skip Exercise")
        }
    }
}

// Pick a random message and tip
private fun getRandomMotivationAndTip(): Pair<String, String> {
    val randomMessageIndex = Random.nextInt(MOTIVATIONAL_MESSAGES.size)
    val randomTipIndex = Random.nextInt(PROCRASTINATION_TIPS.size)

    return Pair(MOTIVATIONAL_MESSAGES[randomMessageIndex], PROCRASTINATION_TIPS[randomTipIndex])
}

private val MOTIVATIONAL_MESSAGES = listOf(
    "You don't have to be great to start, but you have to start to be great.",
    "The secret of getting ahead is getting started.",
    "Don't wait for motivation. Just start and let motivation catch up with you.",
    "Action is the antidote to procrastination.",
    "The best way to predict your future is to create it.",
    "Small progress is still progress.",
    "Focus on progress, not perfection.",
    "Your future self is watching you right now through memories.",
    "The only way to do great work is to love what you do.",
    "The hardest part of any journey is taking the first step."
)

private val PROCRASTINATION_TIPS = listOf(
    "Break your task into smaller, manageable chunks.",
    "Set a timer for just 5 minutes of focused work.",
    "Remove distractions from your environment.",
    "Use the Pomodoro Technique: 25 minutes of work followed by a 5-minute break.",
    "Reward yourself after completing a task.",
    "Tell someone about your goals to create accountability.",
    "Visualize how you'll feel after completing the task.",
    "Start with the easiest part of the task to build momentum.",
    "Create a dedicated workspace that signals 'it's time to work'.",
    "Practice self-compassion if you slip up - everyone procrastinates sometimes."
)

// Create list of possible actions
private fun generateActionSuggestions(): List<String> {
    return listOf(
        "Start a 25-minute Pomodoro session",
        "Try a 2-minute breathing exercise",
        "Take a quick stretch break"
    )
}
