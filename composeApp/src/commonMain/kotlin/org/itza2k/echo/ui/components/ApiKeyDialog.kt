package org.itza2k.echo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.itza2k.echo.data.api.AIModel
import org.itza2k.echo.data.api.ApiKeyManager

/**
 * Dialog for collecting and displaying API keys for different AI models.
 * 
 * @param onDismiss Callback for when the dialog is dismissed without saving
 * @param onApiKeySaved Callback for when the API key is saved
 */
@Composable
fun ApiKeyDialog(
    onDismiss: () -> Unit,
    onApiKeySaved: () -> Unit
) {
    // Get the current API keys if they exist
    val currentClaudeApiKey by ApiKeyManager.instance.claudeApiKey.collectAsState()
    val currentGeminiApiKey by ApiKeyManager.instance.geminiApiKey.collectAsState()
    val currentSelectedModel by ApiKeyManager.instance.selectedModel.collectAsState()

    // State for the dialog
    var selectedTabIndex by remember { mutableStateOf(if (currentSelectedModel == AIModel.CLAUDE) 0 else 1) }
    var claudeApiKey by remember { mutableStateOf("") }
    var geminiApiKey by remember { mutableStateOf("") }
    var isClaudeError by remember { mutableStateOf(false) }
    var isGeminiError by remember { mutableStateOf(false) }
    var showClaudeApiKey by remember { mutableStateOf(false) }
    var showGeminiApiKey by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf(currentSelectedModel) }

    // Initialize with current API keys if available
    LaunchedEffect(currentClaudeApiKey) {
        if (!currentClaudeApiKey.isNullOrBlank()) {
            claudeApiKey = currentClaudeApiKey!!
        }
    }

    LaunchedEffect(currentGeminiApiKey) {
        if (!currentGeminiApiKey.isNullOrBlank()) {
            geminiApiKey = currentGeminiApiKey!!
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "AI Model Settings",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Tab row for selecting between Claude and Gemini
                TabRow(
                    selectedTabIndex = selectedTabIndex
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Claude") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Gemini") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> {
                        // Claude API Key section
                        Text(
                            text = "Enter your Claude API key to use Anthropic's Claude AI model. " +
                                    "Your key will be stored securely on your device and never shared.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Claude API Key input field with show/hide toggle
                        OutlinedTextField(
                            value = claudeApiKey,
                            onValueChange = { 
                                claudeApiKey = it
                                isClaudeError = false
                            },
                            label = { Text("Claude API Key") },
                            placeholder = { Text("Enter your Claude API key") },
                            visualTransformation = if (showClaudeApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = isClaudeError,
                            supportingText = {
                                if (isClaudeError) {
                                    Text(
                                        text = "API key cannot be empty",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                TextButton(
                                    onClick = { showClaudeApiKey = !showClaudeApiKey },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(if (showClaudeApiKey) "Hide" else "Show")
                                }
                            }
                        )
                    }
                    1 -> {
                        // Gemini API Key section
                        Text(
                            text = "Enter your Gemini API key to use Google's Gemini AI model. " +
                                    "Your key will be stored securely on your device and never shared.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gemini API Key input field with show/hide toggle
                        OutlinedTextField(
                            value = geminiApiKey,
                            onValueChange = { 
                                geminiApiKey = it
                                isGeminiError = false
                            },
                            label = { Text("Gemini API Key") },
                            placeholder = { Text("Enter your Gemini API key") },
                            visualTransformation = if (showGeminiApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = isGeminiError,
                            supportingText = {
                                if (isGeminiError) {
                                    Text(
                                        text = "API key cannot be empty",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                TextButton(
                                    onClick = { showGeminiApiKey = !showGeminiApiKey },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(if (showGeminiApiKey) "Hide" else "Show")
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Model selection
                Text(
                    text = "Select AI model to use:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Radio buttons for model selection
                Column(Modifier.selectableGroup()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedModel == AIModel.CLAUDE,
                                onClick = { selectedModel = AIModel.CLAUDE },
                                role = Role.RadioButton
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedModel == AIModel.CLAUDE,
                            onClick = null // null because we're handling the click on the row
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Claude")
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedModel == AIModel.GEMINI,
                                onClick = { selectedModel = AIModel.GEMINI },
                                role = Role.RadioButton
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedModel == AIModel.GEMINI,
                            onClick = null // null because we're handling the click on the row
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false

                    // Validate and save Claude API key if on Claude tab
                    if (selectedTabIndex == 0) {
                        val trimmedClaudeApiKey = claudeApiKey.trim()
                        if (trimmedClaudeApiKey.isBlank()) {
                            isClaudeError = true
                            hasError = true
                        } else {
                            ApiKeyManager.instance.saveClaudeApiKey(trimmedClaudeApiKey)
                        }
                    }

                    // Validate and save Gemini API key if on Gemini tab
                    if (selectedTabIndex == 1) {
                        val trimmedGeminiApiKey = geminiApiKey.trim()
                        if (trimmedGeminiApiKey.isBlank()) {
                            isGeminiError = true
                            hasError = true
                        } else {
                            ApiKeyManager.instance.saveGeminiApiKey(trimmedGeminiApiKey)
                        }
                    }

                    // Set the selected model
                    ApiKeyManager.instance.selectModel(selectedModel)

                    // Only dismiss and call callback if there are no errors
                    if (!hasError) {
                        onApiKeySaved()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Composable function to check if the API key is set and provide a callback for when it's needed
 */
@Composable
fun rememberApiKeyState(
    apiKeyManager: ApiKeyManager,
    onApiKeyNeeded: () -> Unit
): Boolean {
    var isApiKeySet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isApiKeySet = apiKeyManager.hasApiKey()
        if (!isApiKeySet) {
            onApiKeyNeeded()
        }
    }

    return isApiKeySet
}