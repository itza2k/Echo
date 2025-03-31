package org.itza2k.echo.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.itza2k.echo.ui.theme.LocalResonanceColors
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Pomodoro timer states
 */
enum class PomodoroState {
    WORK,
    SHORT_BREAK,
    LONG_BREAK,
    IDLE
}

/**
 * A Pomodoro timer component that implements the Pomodoro Technique
 * for time management and focus enhancement.
 */
@Composable
fun PomodoroTimer(
    modifier: Modifier = Modifier,
    onTimerComplete: (PomodoroState) -> Unit = {},
    workDuration: Int = 25, // minutes
    shortBreakDuration: Int = 5, // minutes
    longBreakDuration: Int = 15, // minutes
    pomodorosUntilLongBreak: Int = 4
) {
    val colors = LocalResonanceColors.current

    // Timer state
    var timerState by remember { mutableStateOf(PomodoroState.IDLE) }
    var isRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableStateOf(workDuration * 60) }
    var completedPomodoros by remember { mutableStateOf(0) }

    // Calculate the current duration based on the timer state
    val currentDurationSeconds = when (timerState) {
        PomodoroState.WORK -> workDuration * 60
        PomodoroState.SHORT_BREAK -> shortBreakDuration * 60
        PomodoroState.LONG_BREAK -> longBreakDuration * 60
        PomodoroState.IDLE -> workDuration * 60
    }

    // Calculate progress
    val progress = if (currentDurationSeconds > 0) {
        remainingSeconds.toFloat() / currentDurationSeconds.toFloat()
    } else {
        0f
    }

    // Animate progress changes
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300, easing = LinearEasing),
        label = "progressAnimation"
    )

    // Timer effect
    LaunchedEffect(isRunning, timerState) {
        if (isRunning) {
            while (remainingSeconds > 0 && isRunning) {
                delay(1.seconds)
                remainingSeconds--
            }

            if (remainingSeconds <= 0 && isRunning) {
                // Timer completed
                when (timerState) {
                    PomodoroState.WORK -> {
                        completedPomodoros++
                        if (completedPomodoros % pomodorosUntilLongBreak == 0) {
                            timerState = PomodoroState.LONG_BREAK
                            remainingSeconds = longBreakDuration * 60
                        } else {
                            timerState = PomodoroState.SHORT_BREAK
                            remainingSeconds = shortBreakDuration * 60
                        }
                    }
                    PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                        timerState = PomodoroState.WORK
                        remainingSeconds = workDuration * 60
                    }
                    PomodoroState.IDLE -> {
                        // Should not happen, but reset to work
                        timerState = PomodoroState.WORK
                        remainingSeconds = workDuration * 60
                    }
                }

                onTimerComplete(timerState)
            }
        }
    }

    // Format time as MM:SS
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    // State color
    val stateColor = when (timerState) {
        PomodoroState.WORK -> colors.primary
        PomodoroState.SHORT_BREAK -> Color(0xFF4CAF50) // Green
        PomodoroState.LONG_BREAK -> Color(0xFF2196F3) // Blue
        PomodoroState.IDLE -> colors.onSurface.copy(alpha = 0.5f)
    }

    // State text
    val stateText = when (timerState) {
        PomodoroState.WORK -> "Focus"
        PomodoroState.SHORT_BREAK -> "Short Break"
        PomodoroState.LONG_BREAK -> "Long Break"
        PomodoroState.IDLE -> "Ready"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timer display
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Progress circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background circle
                drawArc(
                    color = colors.onSurface.copy(alpha = 0.1f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                // Progress arc
                drawArc(
                    color = stateColor,
                    startAngle = 270f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Time text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = timeText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stateText,
                    fontSize = 14.sp,
                    color = stateColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pomodoros: $completedPomodoros",
                    fontSize = 12.sp,
                    color = colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start/Pause button
            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isRunning) colors.error else colors.primary
                )
            ) {
                if (isRunning) {
                    Text(
                        text = "||",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = colors.onPrimary
                    )
                }
            }

            // Reset button
            IconButton(
                onClick = {
                    isRunning = false
                    timerState = PomodoroState.WORK
                    remainingSeconds = workDuration * 60
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = colors.onSurface
                )
            }

            // Skip button (to next state)
            IconButton(
                onClick = {
                    isRunning = false
                    when (timerState) {
                        PomodoroState.WORK -> {
                            if (completedPomodoros % pomodorosUntilLongBreak == pomodorosUntilLongBreak - 1) {
                                timerState = PomodoroState.LONG_BREAK
                                remainingSeconds = longBreakDuration * 60
                            } else {
                                timerState = PomodoroState.SHORT_BREAK
                                remainingSeconds = shortBreakDuration * 60
                            }
                            completedPomodoros++
                        }
                        PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK, PomodoroState.IDLE -> {
                            timerState = PomodoroState.WORK
                            remainingSeconds = workDuration * 60
                        }
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Skip",
                    tint = colors.onSurface
                )
            }
        }
    }
}
