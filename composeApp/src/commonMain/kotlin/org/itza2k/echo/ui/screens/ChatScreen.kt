package org.itza2k.echo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.itza2k.echo.data.api.AIModel
import org.itza2k.echo.data.api.ApiKeyManager
import org.itza2k.echo.data.api.ClaudeApiClient
import org.itza2k.echo.data.api.GeminiApiClient
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.ui.components.ApiKeyDialog

/**
 * Data class representing a chat message
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)

/**
 * The Chat screen of the Echo app.
 * Provides an AI assistant that can help with task management and prioritization.
 */
@Composable
fun ChatScreen(
    apiKeyManager: ApiKeyManager,
    tasks: List<Task>,
    onTaskPrioritized: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    // Create API clients
    val claudeApiClient = remember { ClaudeApiClient(apiKeyManager) }
    val geminiApiClient = remember { GeminiApiClient(apiKeyManager) }

    // Get the currently selected AI model
    val selectedModel by apiKeyManager.selectedModel.collectAsState()

    // State for chat messages
    val messages = remember { mutableStateListOf<ChatMessage>() }

    // State for text input
    var inputText by remember { mutableStateOf("") }

    // State for API key dialog
    var showApiKeyDialog by remember { mutableStateOf(false) }

    // State for loading indicator
    var isLoading by remember { mutableStateOf(false) }

    // Check if API keys are set based on selected model
    val isClaudeApiKeySet by apiKeyManager.isClaudeApiKeySet.collectAsState()
    val isGeminiApiKeySet by apiKeyManager.isGeminiApiKeySet.collectAsState()
    val isCurrentModelApiKeySet = when (selectedModel) {
        AIModel.CLAUDE -> isClaudeApiKeySet
        AIModel.GEMINI -> isGeminiApiKeySet
    }

    // Scroll state for the chat list
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Add welcome message when the screen is first shown or when the model changes
    LaunchedEffect(Unit, selectedModel) {
        if (messages.isEmpty()) {
            isLoading = true
            try {
                // Generate a personalized greeting based on tasks and selected model
                val greeting = when (selectedModel) {
                    AIModel.CLAUDE -> claudeApiClient.generateInitialGreeting(tasks)
                    AIModel.GEMINI -> geminiApiClient.generateInitialGreeting(tasks)
                }
                messages.add(
                    ChatMessage(
                        id = "welcome",
                        content = greeting,
                        isFromUser = false
                    )
                )
            } catch (e: Exception) {
                // Fallback to static greeting if API call fails
                val modelName = when (selectedModel) {
                    AIModel.CLAUDE -> "Claude"
                    AIModel.GEMINI -> "Gemini"
                }
                messages.add(
                    ChatMessage(
                        id = "welcome",
                        content = "Hello! I'm Echo (powered by $modelName), your AI assistant. I can help you prioritize tasks, provide guidance, and answer questions. How can I help you today?",
                        isFromUser = false
                    )
                )
            } finally {
                isLoading = false
            }
        }
    }

    // Scroll to bottom when new messages are added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                scrollState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    // Show API key dialog if not set for the current model
    LaunchedEffect(isCurrentModelApiKeySet) {
        if (!isCurrentModelApiKeySet) {
            showApiKeyDialog = true
        }
    }

    // Main content
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Chat header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Assistant icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and status
                Column {
                    Text(
                        text = "Echo AI Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Show which model is being used
                        Text(
                            text = "Using: ${selectedModel.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Status indicator
                        Text(
                            text = if (isCurrentModelApiKeySet) "Online" else "Offline - API Key Required",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCurrentModelApiKeySet) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Settings button
                IconButton(
                    onClick = { showApiKeyDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "AI Model Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // API key not set message
        AnimatedVisibility(
            visible = !isCurrentModelApiKeySet,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "Please set your API key to enable AI assistant features",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Chat messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message = message)
                }
            }

            // Loading indicator for initial message
            if (isLoading && messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connecting to Echo AI...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Loading indicator for new messages
            else if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Input area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Text input field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    enabled = isCurrentModelApiKeySet,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                Button(
                    onClick = {
                        if (inputText.isNotEmpty() && isCurrentModelApiKeySet) {
                            // Add user message
                            val userMessage = ChatMessage(
                                id = "user-${messages.size}",
                                content = inputText,
                                isFromUser = true
                            )
                            messages.add(userMessage)

                            // Clear input and show loading
                            val userInput = inputText
                            inputText = ""
                            isLoading = true

                            // Make API call in coroutine
                            coroutineScope.launch {
                                try {
                                    // Get response from the selected AI model
                                    val aiResponse = when (selectedModel) {
                                        AIModel.CLAUDE -> claudeApiClient.sendMessage(userInput, tasks)
                                        AIModel.GEMINI -> geminiApiClient.sendMessage(userInput, tasks)
                                    }

                                    // Add AI response
                                    messages.add(
                                        ChatMessage(
                                            id = "ai-${messages.size}",
                                            content = aiResponse,
                                            isFromUser = false
                                        )
                                    )
                                } catch (e: Exception) {
                                    // Handle error
                                    val modelName = when (selectedModel) {
                                        AIModel.CLAUDE -> "Claude"
                                        AIModel.GEMINI -> "Gemini"
                                    }
                                    messages.add(
                                        ChatMessage(
                                            id = "error-${messages.size}",
                                            content = "Sorry, I encountered an error with the $modelName API: ${e.message}. Please check your API key and try again.",
                                            isFromUser = false
                                        )
                                    )
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = inputText.isNotEmpty() && isCurrentModelApiKeySet && !isLoading,
                    shape = CircleShape,
                    contentPadding = PaddingValues(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }
    }

    // Show API key dialog if needed
    if (showApiKeyDialog) {
        ApiKeyDialog(
            onDismiss = { showApiKeyDialog = false },
            onApiKeySaved = { showApiKeyDialog = false }
        )
    }
}

/**
 * Composable for displaying a chat message
 */
@Composable
private fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for AI messages
        if (!message.isFromUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "E",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        // Message bubble
        Surface(
            shape = RoundedCornerShape(
                topStart = if (message.isFromUser) 16.dp else 4.dp,
                topEnd = if (message.isFromUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if (message.isFromUser) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = if (message.isFromUser) 0.dp else 2.dp,
            shadowElevation = if (message.isFromUser) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (message.isFromUser) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Spacer for user messages to balance the avatar
        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(40.dp))
        }
    }
}
