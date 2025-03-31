package org.itza2k.echo.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.ui.components.TaskList

/**
 * The Plan screen of the Echo app.
 * Allows users to add, view, and organize goals/tasks.
 * Redesigned to use Material 3 components and guidelines.
 */
@Composable
fun PlanScreen(
    tasks: List<Task>,
    onTaskComplete: (Task) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            TaskList(
                tasks = tasks,
                onTaskComplete = onTaskComplete,
                onAddTask = onAddTask,
                onEditTask = onEditTask,
                onDeleteTask = onDeleteTask,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Add padding for bottom navigation
            )
        }
    }
}