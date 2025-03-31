package org.itza2k.echo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.TimeBlock
import org.itza2k.echo.ui.theme.LocalResonanceColors

/**
 * A component that visualizes the day's timeline with scheduled tasks.
 */
@Composable
fun TimelineVisualization(
    tasks: List<Task>,
    timeBlocks: List<TimeBlock>,
    currentTaskId: String?,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalResonanceColors.current
    
    // Group time blocks by task
    val taskTimeBlocks = remember(tasks, timeBlocks) {
        timeBlocks.groupBy { it.taskId }
    }
    
    // Determine if there are any tasks for today
    val hasTasksForToday = timeBlocks.isNotEmpty()
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Today's Timeline",
            style = MaterialTheme.typography.h6,
            color = colors.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (hasTasksForToday) {
            // Timeline visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Draw the timeline
                Canvas(
                    modifier = Modifier
                        .width(24.dp)
                        .height((tasks.size * 80).dp)
                ) {
                    val centerX = size.width / 2
                    
                    // Draw the vertical line
                    drawLine(
                        color = colors.onSurface.copy(alpha = 0.2f),
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, size.height),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                    
                    // Draw dots for each task
                    tasks.forEachIndexed { index, _ ->
                        val y = index * 80f + 40f
                        
                        drawCircle(
                            color = colors.primary,
                            radius = 8f,
                            center = Offset(centerX, y)
                        )
                    }
                }
                
                // Task cards
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    tasks.forEach { task ->
                        val isCurrentTask = task.id == currentTaskId
                        val isCompleted = task.isCompleted
                        
                        // Determine the background color based on task status
                        val backgroundColor by animateColorAsState(
                            targetValue = when {
                                isCompleted -> colors.primary.copy(alpha = 0.2f)
                                isCurrentTask -> colors.secondary.copy(alpha = 0.2f)
                                else -> colors.surface
                            },
                            label = "taskBackgroundColor"
                        )
                        
                        // Pulsing animation for current task
                        val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.6f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseAnimation"
                        )
                        
                        // Border color for current task
                        val borderColor = if (isCurrentTask) {
                            colors.primary.copy(alpha = pulseAlpha)
                        } else {
                            Color.Transparent
                        }
                        
                        // Task card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTaskClick(task) }
                                .drawBehind {
                                    if (isCurrentTask) {
                                        drawRect(
                                            color = borderColor,
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }
                                },
                            backgroundColor = backgroundColor,
                            elevation = if (isCurrentTask) 4.dp else 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                // Task title
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.subtitle1,
                                    color = colors.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                
                                // Time blocks for this task
                                taskTimeBlocks[task.id]?.let { blocks ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    blocks.forEach { block ->
                                        val startTime = block.startTime.toString().substring(0, 5)
                                        val endTime = block.endTime.toString().substring(0, 5)
                                        
                                        Text(
                                            text = "$startTime - $endTime",
                                            style = MaterialTheme.typography.caption,
                                            color = colors.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                
                                // Completion status
                                if (isCompleted) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = "Completed",
                                        style = MaterialTheme.typography.caption,
                                        color = colors.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = colors.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks scheduled for today",
                    style = MaterialTheme.typography.body1,
                    color = colors.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}