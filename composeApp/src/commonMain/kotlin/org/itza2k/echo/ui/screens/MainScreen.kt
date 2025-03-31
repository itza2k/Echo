package org.itza2k.echo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.data.api.ApiKeyManager
import org.itza2k.echo.data.db.DatabaseHelper
import org.itza2k.echo.data.model.EkoReflection
import org.itza2k.echo.data.model.EkoStatus
import org.itza2k.echo.data.model.Goal
import org.itza2k.echo.data.model.ReflectionType
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.TaskPriority
import org.itza2k.echo.data.model.TimeBlock
import org.itza2k.echo.data.preferences.UserPreferences
import org.itza2k.echo.ui.components.ApiKeyDialog
import org.itza2k.echo.ui.components.MoodEnergyTracker
import org.itza2k.echo.ui.components.PomodoroState
import org.itza2k.echo.ui.components.PomodoroTimer
import org.itza2k.echo.ui.components.ResonanceBackground
import org.itza2k.echo.ui.components.TaskCreationDialog
import org.itza2k.echo.ui.components.ThemeDialog
import org.itza2k.echo.ui.components.ThemeToggle
import org.itza2k.echo.ui.components.WelcomeScreen
import org.itza2k.echo.ui.theme.ThemeMode

/**
 * The main screen of the Echo app with bottom navigation.
 * 
 * @param themeMode The current theme mode (light, dark, or system)
 * @param apiKeyManager The API key manager for Claude API
 * @param onThemeModeChange Callback for when the theme mode is changed
 */
@Composable
fun MainScreen(
    themeMode: ThemeMode,
    apiKeyManager: ApiKeyManager,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    // Get the database helper
    val databaseHelper = remember { DatabaseHelper.getInstance() }

    // Get the user preferences
    val userPreferences = remember { UserPreferences.getInstance() }

    // Collect data from the database
    val goals by databaseHelper.goals.collectAsState()
    val tasks by databaseHelper.tasks.collectAsState()
    val timeBlocks by databaseHelper.timeBlocks.collectAsState()

    // Collect user preferences
    val hasCompletedOnboarding by userPreferences.hasCompletedOnboarding.collectAsState()
    val storedUserName by userPreferences.userName.collectAsState()

    // Initialize the database if needed
    LaunchedEffect(Unit) {
        databaseHelper.initializeDatabase()
    }

    // Create a sample EkoStatus and EkoReflection for demonstration
    // In a real app, these would come from a different source
    val now = Clock.System.now()
    val currentTask = tasks.firstOrNull { !it.isCompleted } 
    val currentTimeBlock = if (currentTask != null) {
        timeBlocks.firstOrNull { it.taskId == currentTask.id && !it.isCompleted }
    } else {
        null
    }

    val ekoStatus = remember(currentTask, currentTimeBlock) {
        EkoStatus(
            currentTask = currentTask,
            currentTimeBlock = currentTimeBlock,
            message = if (currentTask != null) {
                "Echo is focused on ${currentTask.title}"
            } else {
                "Echo is ready to help you with your tasks"
            }
        )
    }

    val ekoReflection = remember(currentTask) {
        EkoReflection(
            id = "reflection1",
            message = if (currentTask != null) {
                "I'm currently helping you with ${currentTask.title}. ${currentTask.description}"
            } else {
                "I'm ready to help you with your tasks. What would you like to work on today?"
            },
            type = ReflectionType.NARRATIVE,
            createdAt = now
        )
    }

    // Online status with toggle
    var isOnline by remember { mutableStateOf(true) }

    // Current screen
    var currentScreen by remember { mutableStateOf(Screen.FOCUS) }

    // Dialog states
    var showTaskCreationDialog by remember { mutableStateOf(false) }
    var showPomodoroTimer by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Snackbar host state for notifications
    val snackbarHostState = remember { SnackbarHostState() }

    // Check if API key is set
    val isApiKeySet by apiKeyManager.isApiKeySet.collectAsState()

    // Show API key dialog if not set
    LaunchedEffect(isApiKeySet) {
        if (!isApiKeySet) {
            showApiKeyDialog = true
        }
    }

    // User name state - use stored name if available
    var userName by remember { mutableStateOf(storedUserName ?: "") }

    // Show welcome screen or main UI
    if (!hasCompletedOnboarding) {
        Box(modifier = Modifier.fillMaxSize()) {
            WelcomeScreen(
                onGetStarted = { name ->
                    userName = name
                    // Store the user name in persistent storage
                    userPreferences.setUserName(name)
                    // Mark onboarding as completed
                    userPreferences.completeOnboarding()
                }
            )
        }
        return
    }

    // State to track if we should show a success message
    var showApiKeySavedMessage by remember { mutableStateOf(false) }

    // Show success message when API key is saved
    LaunchedEffect(showApiKeySavedMessage) {
        if (showApiKeySavedMessage) {
            snackbarHostState.showSnackbar("API key saved successfully")
            showApiKeySavedMessage = false
        }
    }

    // Show API key dialog if needed
    if (showApiKeyDialog) {
        ApiKeyDialog(
            onDismiss = { showApiKeyDialog = false },
            onApiKeySaved = { 
                showApiKeyDialog = false
                showApiKeySavedMessage = true
            }
        )
    }

    // Show theme dialog if needed
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = themeMode,
            onThemeChange = onThemeModeChange,
            onDismiss = { showThemeDialog = false }
        )
    }

    // Create the main UI with bottom navigation
    ResonanceBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // Enhanced top bar with app title and settings button
                EchoTopBarWithSettings(
                    onSettingsClick = { showThemeDialog = true }
                )
            },
            bottomBar = {
                // Enhanced bottom navigation with Material 3 styling
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    Icons.Default.Star, 
                                    contentDescription = "Focus",
                                    tint = if (currentScreen == Screen.FOCUS) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            label = { 
                                Text(
                                    "Focus",
                                    color = if (currentScreen == Screen.FOCUS) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            selected = currentScreen == Screen.FOCUS,
                            onClick = { currentScreen = Screen.FOCUS }
                        )
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    Icons.Default.List, 
                                    contentDescription = "Plan",
                                    tint = if (currentScreen == Screen.PLAN) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            label = { 
                                Text(
                                    "Plan",
                                    color = if (currentScreen == Screen.PLAN) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            selected = currentScreen == Screen.PLAN,
                            onClick = { currentScreen = Screen.PLAN }
                        )
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    Icons.Default.CheckCircle, 
                                    contentDescription = "Review",
                                    tint = if (currentScreen == Screen.REVIEW) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            label = { 
                                Text(
                                    "Review",
                                    color = if (currentScreen == Screen.REVIEW) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            selected = currentScreen == Screen.REVIEW,
                            onClick = { currentScreen = Screen.REVIEW }
                        )
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    Icons.Default.Send, 
                                    contentDescription = "Chat",
                                    tint = if (currentScreen == Screen.CHAT) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            label = { 
                                Text(
                                    "Chat",
                                    color = if (currentScreen == Screen.CHAT) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                ) 
                            },
                            selected = currentScreen == Screen.CHAT,
                            onClick = { currentScreen = Screen.CHAT }
                        )
                    }
                }
            }
        ) { paddingValues ->
            // Content based on current screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.FOCUS -> FocusScreen(
                        ekoStatus = ekoStatus,
                        ekoReflection = ekoReflection,
                        tasks = tasks,
                        timeBlocks = timeBlocks,
                        isOnline = isOnline,
                        onTimerClick = { showPomodoroTimer = true },
                        onTaskClick = { /* Handle task click */ }
                    )
                    Screen.PLAN -> PlanScreen(
                        tasks = tasks,
                        onTaskComplete = { task ->
                            // Update the completed status of the task in the database
                            kotlinx.coroutines.MainScope().launch {
                                databaseHelper.updateTaskCompletion(task.id, !task.isCompleted)
                            }
                        },
                        onAddTask = { showTaskCreationDialog = true },
                        onEditTask = { task ->
                            // Placeholder for task editing functionality
                            // In a real implementation, this would show a dialog to edit the task
                        },
                        onDeleteTask = { task ->
                            // TODO: Implement task deletion functionality
                            // For now, we'll just show a message that this feature is not implemented
                            kotlinx.coroutines.MainScope().launch {
                                snackbarHostState.showSnackbar("Delete task functionality not implemented yet")
                            }
                        }
                    )
                    Screen.REVIEW -> {
                        // Group completed tasks by date using the task's creation date
                        val completedTasksByDate = tasks
                            .filter { it.isCompleted }
                            .groupBy { 
                                // Extract date from the task's createdAt timestamp
                                val localDateTime = it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
                                localDateTime.date
                            }

                        // Calculate tasks completed today
                        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val tasksCompletedToday = completedTasksByDate[today]?.size ?: 0

                        // Calculate tasks completed this week (simplified)
                        val tasksCompletedThisWeek = completedTasksByDate.values.flatten().size

                        // Total tasks completed
                        val totalTasksCompleted = tasks.count { it.isCompleted }

                        ReviewScreen(
                            completedTasks = completedTasksByDate,
                            tasksCompletedToday = tasksCompletedToday,
                            tasksCompletedThisWeek = tasksCompletedThisWeek,
                            totalTasksCompleted = totalTasksCompleted
                        )
                    }
                    Screen.CHAT -> {
                        ChatScreen(
                            apiKeyManager = apiKeyManager,
                            tasks = tasks,
                            onTaskPrioritized = { task ->
                                // Handle task prioritization
                                kotlinx.coroutines.MainScope().launch {
                                    snackbarHostState.showSnackbar("Task prioritized: ${task.title}")
                                }
                            }
                        )
                    }
                }
            }
        }

        // Show task creation dialog if requested
        if (showTaskCreationDialog) {
            TaskCreationDialog(
                onDismiss = { showTaskCreationDialog = false },
                onTaskCreated = { newTask, timeBlock ->
                    // Add the new task to the database
                    kotlinx.coroutines.MainScope().launch {
                        databaseHelper.addTask(newTask)

                        // Add the time block to the database if it's not null
                        if (timeBlock != null) {
                            databaseHelper.addTimeBlock(timeBlock)
                        }
                    }
                },
                goalId = goals.firstOrNull()?.id ?: ""
            )
        }

        // Show Pomodoro timer if requested
        if (showPomodoroTimer) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Semi-transparent background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    // Close button
                    Box(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = { showPomodoroTimer = false }
                        ) {
                            Text("✕", fontSize = 24.sp)
                        }
                    }

                    // Pomodoro timer
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        PomodoroTimer(
                            onTimerComplete = { state ->
                                // Update the current task status based on timer completion
                                if (state == PomodoroState.WORK) {
                                    // A work session was completed
                                    // Could update task progress here
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Enum representing the different screens in the app.
 */
private enum class Screen {
    FOCUS, PLAN, REVIEW, CHAT
}

// Sample data creation has been removed as we now use the database for all data

/**
 * Enhanced top bar with app title and theme toggle
 */
@Composable
private fun EchoTopBar(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App title
            Text(
                text = "Echo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Theme toggle
            ThemeToggle(
                currentTheme = themeMode,
                onThemeChange = onThemeModeChange
            )
        }
    }
}

/**
 * Enhanced top bar with app title and settings button
 */
@Composable
private fun EchoTopBarWithSettings(
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App title
            Text(
                text = "Echo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Settings button
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
