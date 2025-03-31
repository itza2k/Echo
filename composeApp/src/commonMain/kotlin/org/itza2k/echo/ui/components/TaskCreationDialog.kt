package org.itza2k.echo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
// No icons needed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.TaskPriority
import org.itza2k.echo.data.model.TimeBlock
import java.util.UUID

/**
 * A dialog for creating new tasks with Material 3 components.
 * Allows scheduling the task by creating a time block.
 */
@Composable
fun TaskCreationDialog(
    onDismiss: () -> Unit,
    onTaskCreated: (Task, TimeBlock?) -> Unit,
    goalId: String
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var estimatedDuration by remember { mutableStateOf(60f) } // in minutes
    var startTime by remember { mutableStateOf(LocalTime(9, 0)) }
    var isSchedulingEnabled by remember { mutableStateOf(true) }
    var isCreating by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Create New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Priority selection with segmented buttons
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Segmented button for priority
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PrioritySegmentedButton(
                        priority = priority,
                        onPrioritySelected = { priority = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estimated duration slider
                Text(
                    text = "Estimated Duration: ${estimatedDuration.toInt()} minutes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = estimatedDuration,
                    onValueChange = { estimatedDuration = it },
                    valueRange = 15f..180f,
                    steps = 11, // 15-minute increments
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Scheduling checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSchedulingEnabled,
                        onCheckedChange = { isSchedulingEnabled = it }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Schedule this task",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Time picker button (only shown if scheduling is enabled)
                if (isSchedulingEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏰",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Start Time: ${startTime.hour}:${startTime.minute.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // In a real implementation, this would open a time picker
                        TextButton(onClick = {
                            // For demo purposes, just increment the hour
                            startTime = LocalTime(
                                (startTime.hour + 1) % 24,
                                startTime.minute
                            )
                        }) {
                            Text("Change")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                isCreating = true
                                val now = Clock.System.now()
                                val taskId = UUID.randomUUID().toString()

                                // Create the task
                                val newTask = Task(
                                    id = taskId,
                                    goalId = goalId,
                                    title = title,
                                    description = description,
                                    isCompleted = false,
                                    priority = priority,
                                    createdAt = now,
                                    updatedAt = now
                                )

                                // Create a time block if scheduling is enabled
                                val timeBlock = if (isSchedulingEnabled) {
                                    // Calculate end time based on estimated duration
                                    val durationMinutes = estimatedDuration.toInt()
                                    val endHour = (startTime.hour + durationMinutes / 60) % 24
                                    val endMinute = (startTime.minute + durationMinutes % 60) % 60
                                    val endTime = LocalTime(endHour, endMinute)

                                    // Create the time block
                                    TimeBlock(
                                        id = UUID.randomUUID().toString(),
                                        taskId = taskId,
                                        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                                        startTime = startTime,
                                        endTime = endTime,
                                        isCompleted = false
                                    )
                                } else {
                                    null
                                }

                                // Pass both the task and time block to the callback
                                onTaskCreated(newTask, timeBlock)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && !isCreating
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

/**
 * A segmented button for selecting task priority.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrioritySegmentedButton(
    priority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    val options = listOf("Low", "Medium", "High")
    val selectedIndex = when (priority) {
        TaskPriority.LOW -> 0
        TaskPriority.MEDIUM -> 1
        TaskPriority.HIGH -> 2
    }

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth(0.8f)) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = {
                    val newPriority = when (index) {
                        0 -> TaskPriority.LOW
                        1 -> TaskPriority.MEDIUM
                        else -> TaskPriority.HIGH
                    }
                    onPrioritySelected(newPriority)
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(label)
            }
        }
    }
}
