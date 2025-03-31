package org.itza2k.echo.data.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Represents a mood entry in the Echo app.
 * Users can track their mood throughout the day to identify patterns.
 */
data class MoodEntry(
    val id: String,
    val level: MoodLevel,
    val note: String = "",
    val date: LocalDate,
    val time: LocalTime,
    val createdAt: Instant = Instant.DISTANT_PAST
)

/**
 * Represents an energy level entry in the Echo app.
 * Users can track their energy levels throughout the day to identify optimal work times.
 */
data class EnergyEntry(
    val id: String,
    val level: EnergyLevel,
    val note: String = "",
    val date: LocalDate,
    val time: LocalTime,
    val createdAt: Instant = Instant.DISTANT_PAST
)

/**
 * Mood levels that users can select.
 */
enum class MoodLevel(val emoji: String, val description: String) {
    VERY_BAD("😞", "Very Bad"),
    BAD("😔", "Bad"),
    NEUTRAL("😐", "Neutral"),
    GOOD("🙂", "Good"),
    VERY_GOOD("😄", "Very Good")
}

/**
 * Energy levels that users can select.
 */
enum class EnergyLevel(val emoji: String, val description: String) {
    VERY_LOW("🔋", "Very Low"),
    LOW("🔋🔋", "Low"),
    MEDIUM("🔋🔋🔋", "Medium"),
    HIGH("🔋🔋🔋🔋", "High"),
    VERY_HIGH("🔋🔋🔋🔋🔋", "Very High")
}

/**
 * Combined mood and energy entry for a specific time.
 */
data class MoodEnergyEntry(
    val id: String,
    val moodLevel: MoodLevel,
    val energyLevel: EnergyLevel,
    val note: String = "",
    val date: LocalDate,
    val time: LocalTime,
    val createdAt: Instant = Instant.DISTANT_PAST
)