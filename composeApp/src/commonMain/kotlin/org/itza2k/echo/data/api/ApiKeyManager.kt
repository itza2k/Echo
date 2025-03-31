package org.itza2k.echo.data.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Enum class representing the different AI models available
 */
enum class AIModel {
    CLAUDE,
    GEMINI
}

/**
 * Manager for handling API key storage and retrieval for different AI models
 * 
 * TODO: Implement proper persistence for API keys using the database.
 * A schema file (ApiKey.sq) has been created for this purpose, but
 * the database needs to be rebuilt to include it. Once that's done,
 * this class should be updated to use the database for persistence.
 */
class ApiKeyManager {
    companion object {
        // Singleton instance
        val instance by lazy { ApiKeyManager() }
    }

    // In-memory storage of the Claude API key
    private val _claudeApiKey = MutableStateFlow<String?>(null)
    val apiKey: StateFlow<String?> = _claudeApiKey.asStateFlow() // For backward compatibility
    val claudeApiKey: StateFlow<String?> = _claudeApiKey.asStateFlow()

    // In-memory storage of the Gemini API key
    private val _geminiApiKey = MutableStateFlow<String?>(null)
    val geminiApiKey: StateFlow<String?> = _geminiApiKey.asStateFlow()

    // Currently selected AI model
    private val _selectedModel = MutableStateFlow(AIModel.CLAUDE)
    val selectedModel: StateFlow<AIModel> = _selectedModel.asStateFlow()

    // Flag to track if the Claude API key has been set
    private val _isClaudeApiKeySet = MutableStateFlow(false)
    val isApiKeySet: StateFlow<Boolean> = _isClaudeApiKeySet.asStateFlow() // For backward compatibility
    val isClaudeApiKeySet: StateFlow<Boolean> = _isClaudeApiKeySet.asStateFlow()

    // Flag to track if the Gemini API key has been set
    private val _isGeminiApiKeySet = MutableStateFlow(false)
    val isGeminiApiKeySet: StateFlow<Boolean> = _isGeminiApiKeySet.asStateFlow()

    /**
     * Save the Claude API key
     * 
     * Trims the API key and removes any potential illegal characters
     * that could cause issues in HTTP headers.
     * 
     * TODO: Persist the API key to the database once it's available
     */
    fun saveApiKey(apiKey: String) {
        saveClaudeApiKey(apiKey) // For backward compatibility
    }

    /**
     * Save the Claude API key
     */
    fun saveClaudeApiKey(apiKey: String) {
        // Trim the API key and remove any potential illegal characters
        val sanitizedKey = apiKey.trim()
        _claudeApiKey.value = sanitizedKey
        _isClaudeApiKeySet.value = true
    }

    /**
     * Save the Gemini API key
     */
    fun saveGeminiApiKey(apiKey: String) {
        // Trim the API key and remove any potential illegal characters
        val sanitizedKey = apiKey.trim()
        _geminiApiKey.value = sanitizedKey
        _isGeminiApiKeySet.value = true
    }

    /**
     * Set the currently selected AI model
     */
    fun selectModel(model: AIModel) {
        _selectedModel.value = model
    }

    /**
     * Check if an API key is already stored
     * 
     * TODO: Check the database for an API key once it's available
     */
    fun hasApiKey(): Boolean {
        return _isClaudeApiKeySet.value // For backward compatibility
    }

    /**
     * Check if the Claude API key is set
     */
    fun hasClaudeApiKey(): Boolean {
        return _isClaudeApiKeySet.value
    }

    /**
     * Check if the Gemini API key is set
     */
    fun hasGeminiApiKey(): Boolean {
        return _isGeminiApiKeySet.value
    }

    /**
     * Clear the stored API keys
     * 
     * TODO: Delete the API keys from the database once it's available
     */
    fun clearApiKey() {
        clearClaudeApiKey() // For backward compatibility
    }

    /**
     * Clear the stored Claude API key
     */
    fun clearClaudeApiKey() {
        _claudeApiKey.value = null
        _isClaudeApiKeySet.value = false
    }

    /**
     * Clear the stored Gemini API key
     */
    fun clearGeminiApiKey() {
        _geminiApiKey.value = null
        _isGeminiApiKeySet.value = false
    }
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
