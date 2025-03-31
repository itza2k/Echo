package org.itza2k.echo.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.MoodEnergyEntry

/**
 * Client for interacting with the Claude API
 */
class ClaudeApiClient(private val apiKeyManager: ApiKeyManager) {

    companion object {
        private const val BASE_URL = "https://api.anthropic.com/v1"
        private const val API_VERSION = "2023-06-01" // Update this as needed
        private const val MODEL = "claude-3-7-sonnet-20250219" // Using the latest model
    }

    // Reflection prompt templates
    private val REFLECTION_PROMPT = """
        Based on the user's mood and energy tracking data, provide a thoughtful reflection that:
        1. Acknowledges their current emotional and energy state
        2. Identifies potential patterns or insights
        3. Offers personalized suggestions for maintaining or improving their wellbeing
        4. Connects their state to their productivity and task management

        Keep your response concise (3-5 sentences) but insightful and empathetic.
    """.trimIndent()

    // Create Ktor HTTP client
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    /**
     * Send a message to Claude and get a response
     * 
     * @param userMessage The message from the user
     * @param tasks List of tasks to provide as context
     * @return The response from Claude
     */
    suspend fun sendMessage(userMessage: String, tasks: List<Task>): String {
        // Get Claude API key
        val apiKey = apiKeyManager.claudeApiKey.first() ?: throw IllegalStateException("Claude API key not set")

        // Build system prompt with task context
        val systemPrompt = buildSystemPrompt(tasks)

        // Create request body
        val requestBody = MessageRequest(
            model = MODEL,
            messages = listOf(
                Message(role = "system", content = systemPrompt),
                Message(role = "user", content = userMessage)
            ),
            maxTokens = 1024
        )

        try {
            // Make sure the API key is properly sanitized before using it in headers
            val sanitizedApiKey = apiKey.trim()

            // Make API request
            val response = client.post("$BASE_URL/messages") {
                contentType(ContentType.Application.Json)
                header("x-api-key", sanitizedApiKey)
                header("anthropic-version", API_VERSION)
                setBody(requestBody)
            }

            // Parse response
            val messageResponse = response.body<MessageResponse>()
            return messageResponse.content.firstOrNull()?.text ?: 
                "I'm sorry, but I couldn't generate a response at this time. This might be due to a temporary issue with the Claude AI service. Please try again in a few moments."

        } catch (e: Exception) {
            // Handle errors gracefully
            return "I'm sorry, but I encountered an error while communicating with the Claude AI service. " +
                   "This might be due to an invalid API key or network issues. " +
                   "Please check your API key and internet connection, then try again."
        }
    }

    /**
     * Build a system prompt that includes context about the user's tasks
     */
    private fun buildSystemPrompt(tasks: List<Task>): String {
        val incompleteTasks = tasks.filter { !it.isCompleted }
        val completedTasks = tasks.filter { it.isCompleted }

        val sb = StringBuilder()
        sb.appendLine("You are Echo, an AI assistant focused on helping users manage their tasks, improve productivity, and overcome procrastination.")
        sb.appendLine("Be concise, helpful, and encouraging in your responses.")

        // Add task context if available
        if (tasks.isNotEmpty()) {
            sb.appendLine("\nHere is the current context about the user's tasks:")

            if (incompleteTasks.isNotEmpty()) {
                sb.appendLine("\nIncomplete tasks:")
                incompleteTasks.forEach { task ->
                    sb.appendLine("- ${task.title} (Priority: ${task.priority}): ${task.description}")
                }
            }

            if (completedTasks.isNotEmpty()) {
                sb.appendLine("\nRecently completed tasks:")
                completedTasks.take(3).forEach { task ->
                    sb.appendLine("- ${task.title}")
                }
            }
        }

        sb.appendLine("\nYou should:")
        sb.appendLine("1. Help the user prioritize their tasks")
        sb.appendLine("2. Provide specific, actionable advice")
        sb.appendLine("3. Offer encouragement and motivation")
        sb.appendLine("4. Suggest techniques to overcome procrastination when relevant")

        return sb.toString()
    }

    /**
     * Generate an initial greeting message that references the user's tasks
     */
    suspend fun generateInitialGreeting(tasks: List<Task>): String {
        val incompleteTasks = tasks.filter { !it.isCompleted }

        if (incompleteTasks.isEmpty()) {
            return "Hello! I'm Echo (powered by Claude), your AI assistant. I'm here to help you manage your tasks and boost your productivity. How can I assist you today?"
        } else {
            val highPriorityTasks = incompleteTasks.filter { it.priority == org.itza2k.echo.data.model.TaskPriority.HIGH }

            return if (highPriorityTasks.isNotEmpty()) {
                "Hello! I'm Echo (powered by Claude), your AI assistant. I notice you have ${incompleteTasks.size} tasks in progress, " +
                "including ${highPriorityTasks.size} high-priority tasks like \"${highPriorityTasks.first().title}\". " +
                "How can I help you make progress today?"
            } else {
                "Hello! I'm Echo (powered by Claude), your AI assistant. I see you have ${incompleteTasks.size} tasks in progress. " +
                "How can I help you prioritize and complete them today?"
            }
        }
    }

    /**
     * Generate a reflection message based on the user's mood and energy tracking data
     * 
     * @param entries List of mood/energy entries to analyze
     * @return A reflection message from Claude
     */
    suspend fun generateReflection(entries: List<MoodEnergyEntry>): String {
        // Get Claude API key
        val apiKey = apiKeyManager.claudeApiKey.first() ?: throw IllegalStateException("Claude API key not set")

        // If no entries, return a generic message
        if (entries.isEmpty()) {
            return "I don't have any mood or energy data to analyze yet. Try tracking your mood and energy levels regularly to get personalized insights."
        }

        // Build a prompt with the user's mood/energy data
        val prompt = buildReflectionPrompt(entries)

        // Create request body
        val requestBody = MessageRequest(
            model = MODEL,
            messages = listOf(
                Message(role = "system", content = prompt),
                Message(role = "user", content = "Please provide a reflection on my mood and energy data.")
            ),
            maxTokens = 1024
        )

        try {
            // Make sure the API key is properly sanitized before using it in headers
            val sanitizedApiKey = apiKey.trim()

            // Make API request
            val response = client.post("$BASE_URL/messages") {
                contentType(ContentType.Application.Json)
                header("x-api-key", sanitizedApiKey)
                header("anthropic-version", API_VERSION)
                setBody(requestBody)
            }

            // Parse response
            val messageResponse = response.body<MessageResponse>()
            return messageResponse.content.firstOrNull()?.text ?: 
                "I couldn't generate a reflection at this time. Please try again later."

        } catch (e: Exception) {
            return "I encountered an error while trying to generate a reflection. Please check your internet connection and try again."
        }
    }

    /**
     * Build a prompt for reflection based on the user's mood/energy data
     */
    private fun buildReflectionPrompt(entries: List<MoodEnergyEntry>): String {
        val sb = StringBuilder()
        sb.appendLine(REFLECTION_PROMPT)
        sb.appendLine("\nHere is the user's mood and energy tracking data:")

        // Sort entries by date/time (most recent first)
        val sortedEntries = entries.sortedByDescending { it.createdAt }

        // Add the most recent 5 entries (or fewer if there aren't that many)
        sortedEntries.take(5).forEachIndexed { index, entry ->
            sb.appendLine("\nEntry ${index + 1}:")
            sb.appendLine("- Date: ${entry.date}")
            sb.appendLine("- Time: ${entry.time}")
            sb.appendLine("- Mood: ${entry.moodLevel.description} (${entry.moodLevel.emoji})")
            sb.appendLine("- Energy: ${entry.energyLevel.description} (${entry.energyLevel.emoji})")
            if (entry.note.isNotBlank()) {
                sb.appendLine("- Note: ${entry.note}")
            }
        }

        // Add some summary information if there are enough entries
        if (entries.size >= 3) {
            sb.appendLine("\nSummary:")

            // Most common mood
            val moodCounts = entries.groupBy { it.moodLevel }.mapValues { it.value.size }
            val mostCommonMood = moodCounts.maxByOrNull { it.value }?.key
            if (mostCommonMood != null) {
                sb.appendLine("- Most common mood: ${mostCommonMood.description} (${mostCommonMood.emoji})")
            }

            // Most common energy level
            val energyCounts = entries.groupBy { it.energyLevel }.mapValues { it.value.size }
            val mostCommonEnergy = energyCounts.maxByOrNull { it.value }?.key
            if (mostCommonEnergy != null) {
                sb.appendLine("- Most common energy level: ${mostCommonEnergy.description} (${mostCommonEnergy.emoji})")
            }
        }

        return sb.toString()
    }
}

/**
 * Data classes for Claude API requests and responses
 */
@Serializable
data class MessageRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("max_tokens") val maxTokens: Int
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class MessageResponse(
    val id: String,
    val content: List<ContentBlock> = emptyList(),
    val model: String? = null,
    val role: String? = null
)

@Serializable
data class ContentBlock(
    val type: String,
    val text: String
)
