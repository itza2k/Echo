package org.itza2k.echo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.data.model.EkoReflection
import org.itza2k.echo.data.model.ReflectionType
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.db.DatabaseHelper
import org.itza2k.echo.ui.components.CompletedTaskList
import org.itza2k.echo.ui.components.SimpleStatsDisplay
import org.itza2k.echo.ui.theme.LocalResonanceColors
import java.util.UUID
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.window.Dialog

/**
 * The Review screen of the Echo app.
 * Provides a sense of accomplishment and enables reflection.
 * Redesigned to use Material 3 components and guidelines.
 */
@Composable
fun ReviewScreen(
    completedTasks: Map<LocalDate, List<Task>>,
    tasksCompletedToday: Int,
    tasksCompletedThisWeek: Int,
    totalTasksCompleted: Int,
    modifier: Modifier = Modifier
) {
    // Get the database helper
    val databaseHelper = remember { DatabaseHelper.getInstance() }

    // State for reflections
    val reflections by databaseHelper.reflections.collectAsState()

    // State for showing saved reflections
    var showSavedReflections by remember { mutableStateOf(false) }

    // Dialog to show saved reflections
    if (showSavedReflections) {
        SavedReflectionsDialog(
            reflections = reflections,
            onDismiss = { showSavedReflections = false }
        )
    }
    val colorScheme = LocalResonanceColors.current
    val scrollState = rememberScrollState()

    // Get current date for personalized greeting
    val now = Clock.System.now()
    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val currentHour = localDateTime.hour

    // Determine greeting based on time of day
    val greeting = when {
        currentHour < 12 -> "Good morning"
        currentHour < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showReflection by remember { mutableStateOf(false) }
    var showTasks by remember { mutableStateOf(false) }

    // Reflection state
    var reflectionText by remember { mutableStateOf("") }

    // Trigger animations sequentially
    LaunchedEffect(Unit) {
        showHeader = true
        delay(300)
        showStats = true
        delay(300)
        showReflection = true
        delay(300)
        showTasks = true
    }

    // Determine motivational message based on task completion
    val motivationalMessage = when {
        tasksCompletedToday > 3 -> "Incredible work today! You're on fire!"
        tasksCompletedToday > 0 -> "Great job making progress today!"
        tasksCompletedThisWeek > 10 -> "You've had a productive week! Keep it up!"
        tasksCompletedThisWeek > 0 -> "You're making steady progress this week."
        totalTasksCompleted > 0 -> "Every completed task is a step forward. You're doing great!"
        else -> "Ready to start your productivity journey? You've got this!"
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .padding(bottom = 64.dp) // Add padding for bottom navigation
            ) {
                // Animated header with personalized greeting
                AnimatedVisibility(
                    visible = showHeader,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$greeting!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Here's your progress so far",
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Motivational message
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = motivationalMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic,
                                color = colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats display with animation
                AnimatedVisibility(
                    visible = showStats,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    SimpleStatsDisplay(
                        tasksCompletedToday = tasksCompletedToday,
                        tasksCompletedThisWeek = tasksCompletedThisWeek,
                        totalTasksCompleted = totalTasksCompleted,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Daily reflection section with animation
                AnimatedVisibility(
                    visible = showReflection,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Daily Reflection",
                                style = MaterialTheme.typography.titleLarge,
                                color = colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Take a moment to reflect on your achievements and challenges today.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = reflectionText,
                                onValueChange = { reflectionText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("What went well? What could be improved?") },
                                minLines = 3,
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Button to view saved reflections
                                TextButton(
                                    onClick = { showSavedReflections = true }
                                ) {
                                    Text("View Past Reflections")
                                }

                                // Button to save current reflection
                                TextButton(
                                    onClick = {
                                        // Create a new reflection with the current text
                                        if (reflectionText.isNotBlank()) {
                                            val reflection = EkoReflection(
                                                id = UUID.randomUUID().toString(),
                                                message = reflectionText,
                                                type = ReflectionType.REFLECTION,
                                                createdAt = Clock.System.now()
                                            )

                                            // Save the reflection
                                            kotlinx.coroutines.MainScope().launch {
                                                databaseHelper.addReflection(reflection)
                                                // Clear the text field
                                                reflectionText = ""
                                            }
                                        }
                                    }
                                ) {
                                    Text("Save Reflection")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Completed tasks list with animation
                AnimatedVisibility(
                    visible = showTasks,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it })
                ) {
                    CompletedTaskList(
                        completedTasks = completedTasks,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * A dialog that displays saved reflections.
 */
@Composable
private fun SavedReflectionsDialog(
    reflections: List<EkoReflection>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Reflections",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                if (reflections.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reflections yet. Start reflecting on your day!",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    // Display reflections
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reflections.sortedByDescending { it.createdAt }) { reflection ->
                            ReflectionItem(reflection = reflection)
                        }
                    }
                }

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

/**
 * A component that displays a single reflection.
 */
@Composable
private fun ReflectionItem(reflection: EkoReflection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Reflection message
            Text(
                text = reflection.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Date and type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatReflectionDate(reflection.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Text(
                    text = reflection.type.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Format a date for display.
 */
private fun formatReflectionDate(instant: Instant): String {
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    return if (dateTime.date == today) {
        "Today at ${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
    } else {
        "${dateTime.date.dayOfMonth}/${dateTime.date.monthNumber}/${dateTime.date.year} at ${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
    }
}
