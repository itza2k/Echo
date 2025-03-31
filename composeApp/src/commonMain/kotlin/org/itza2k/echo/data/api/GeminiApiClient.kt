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
 * Client for interacting with the Google Gemini API
 */
class GeminiApiClient(private val apiKeyManager: ApiKeyManager) {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "models/gemini-2.0-flash" // Using the latest model
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
     * Send a message to Gemini and get a response
     * 
     * @param userMessage The message from the user
     * @param tasks List of tasks to provide as context
     * @return The response from Gemini
     */
    suspend fun sendMessage(userMessage: String, tasks: List<Task>): String {
        // Get API key
        val apiKey = apiKeyManager.geminiApiKey.first() ?: throw IllegalStateException("Gemini API key not set")

        // Build system prompt with task context
        val systemPrompt = buildSystemPrompt(tasks)

        // Create request body
        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = systemPrompt),
                        GeminiPart(text = userMessage)
                    )
                )
            )
        )

        try {
            // Make API request
            val response = client.post("$BASE_URL/$MODEL:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            // Parse response
            val geminiResponse = response.body<GeminiResponse>()
            return geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: 
                "I'm sorry, but I couldn't generate a response at this time. This might be due to a temporary issue with the AI service. Please try again in a few moments."

        } catch (e: Exception) {
            // Handle errors gracefully
            return "I'm sorry, but I encountered an error while communicating with the Gemini AI service. " +
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
            return "Hello! I'm Echo (powered by Gemini), your AI assistant. I'm here to help you manage your tasks and boost your productivity. How can I assist you today?"
        } else {
            val highPriorityTasks = incompleteTasks.filter { it.priority == org.itza2k.echo.data.model.TaskPriority.HIGH }

            return if (highPriorityTasks.isNotEmpty()) {
                "Hello! I'm Echo (powered by Gemini), your AI assistant. I notice you have ${incompleteTasks.size} tasks in progress, " +
                "including ${highPriorityTasks.size} high-priority tasks like \"${highPriorityTasks.first().title}\". " +
                "How can I help you make progress today?"
            } else {
                "Hello! I'm Echo (powered by Gemini), your AI assistant. I see you have ${incompleteTasks.size} tasks in progress. " +
                "How can I help you prioritize and complete them today?"
            }
        }
    }

    /**
     * Generate a reflection message based on the user's mood and energy tracking data
     * 
     * @param entries List of mood/energy entries to analyze
     * @return A reflection message from Gemini
     */
    suspend fun generateReflection(entries: List<MoodEnergyEntry>): String {
        // Get API key
        val apiKey = apiKeyManager.geminiApiKey.first() ?: throw IllegalStateException("Gemini API key not set")

        // If no entries, return a generic message
        if (entries.isEmpty()) {
            return "I don't have any mood or energy data to analyze yet. Try tracking your mood and energy levels regularly to get personalized insights."
        }

        // Build a prompt with the user's mood/energy data
        val prompt = buildReflectionPrompt(entries)

        // Create request body
        val requestBody = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            )
        )

        try {
            // Make API request
            val response = client.post("$BASE_URL/$MODEL:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            // Parse response
            val geminiResponse = response.body<GeminiResponse>()
            return geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: 
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
 * Data classes for Gemini API requests and responses
 */
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)
