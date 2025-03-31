package org.itza2k.echo.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.ui.theme.LocalResonanceColors

/**
 * A component that displays a list of completed tasks, grouped by date.
 */
@Composable
fun CompletedTaskList(
    completedTasks: Map<LocalDate, List<Task>>,
    modifier: Modifier = Modifier
) {
    val colorScheme = LocalResonanceColors.current

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Text(
            text = "Completed Tasks",
            style = MaterialTheme.typography.titleLarge,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )

        // Task list
        if (completedTasks.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                completedTasks.forEach { (date, tasks) ->
                    // Date header
                    item {
                        Text(
                            text = formatDate(date),
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Tasks for this date
                    items(tasks) { task ->
                        CompletedTaskItem(task = task)

                        Divider(
                            color = colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // Spacer between date groups
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
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
                    text = "No completed tasks yet. Complete a task to see it here!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * A single completed task item in the list.
 */
@Composable
private fun CompletedTaskItem(
    task: Task
) {
    val colorScheme = LocalResonanceColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkmark icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = colorScheme.primary,
                modifier = Modifier.size(24.dp)
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
                    color = colorScheme.onSurface
                )

                // Task description (if available)
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Formats a LocalDate into a human-readable string.
 */
private fun formatDate(date: LocalDate): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val yesterday = today.minus(DatePeriod(days = 1))

    return when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            // Format as "Month Day" (e.g., "January 15")
            val month = when (date.monthNumber) {
                1 -> "January"
                2 -> "February"
                3 -> "March"
                4 -> "April"
                5 -> "May"
                6 -> "June"
                7 -> "July"
                8 -> "August"
                9 -> "September"
                10 -> "October"
                11 -> "November"
                12 -> "December"
                else -> "Unknown"
            }
            "$month ${date.dayOfMonth}"
        }
    }
}
