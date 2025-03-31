package org.itza2k.echo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.TaskPriority

/**
 * A component that displays a list of tasks with completion checkboxes.
 * Provides functionality for completing, editing, and deleting tasks.
 * Redesigned to use Material 3 components.
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskComplete: (Task) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Text(
                text = "Your Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )

            // Task list
            if (tasks.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = tasks,
                        key = { task -> task.id } // Use task ID as key for better performance
                    ) { task ->
                        TaskItem(
                            task = task,
                            onTaskComplete = onTaskComplete,
                            onEditTask = { onEditTask(task) },
                            onDeleteTask = { onDeleteTask(task) }
                        )

                        Divider(
                            color = colorScheme.onSurface.copy(alpha = 0.1f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet. Add a task to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Add task button
        FloatingActionButton(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task"
            )
        }
    }
}

/**
 * A single task item in the task list.
 */
@Composable
private fun TaskItem(
    task: Task,
    onTaskComplete: (Task) -> Unit,
    onEditTask: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var showOptions by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { showOptions = !showOptions },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onTaskComplete(task) },
                colors = CheckboxDefaults.colors(
                    checkedColor = colorScheme.primary,
                    uncheckedColor = colorScheme.primary.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Task details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (task.isCompleted) colorScheme.onSurface.copy(alpha = 0.6f) else colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Task description (if available)
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Priority indicator
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (task.priority) {
                                    TaskPriority.HIGH -> colorScheme.error
                                    TaskPriority.MEDIUM -> colorScheme.primary
                                    else -> colorScheme.secondary.copy(alpha = 0.6f)
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Priority text
                    Text(
                        text = when (task.priority) {
                            TaskPriority.HIGH -> "High Priority"
                            TaskPriority.MEDIUM -> "Medium Priority"
                            else -> "Low Priority"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Complete button (for uncompleted tasks)
            AnimatedVisibility(
                visible = !task.isCompleted,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                TextButton(
                    onClick = { onTaskComplete(task) }
                ) {
                    Text(
                        text = "Complete",
                        color = colorScheme.primary
                    )
                }
            }
        }

        // Options for editing and deleting tasks
        AnimatedVisibility(
            visible = showOptions,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // Edit button
                IconButton(
                    onClick = {
                        onEditTask()
                        showOptions = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Task",
                        tint = colorScheme.primary
                    )
                }

                // Delete button
                IconButton(
                    onClick = {
                        onDeleteTask()
                        showOptions = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = colorScheme.error
                    )
                }
            }
        }
    }
}
