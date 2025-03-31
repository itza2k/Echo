package org.itza2k.echo.data.model

// These imports will be available after the build process
// when the kotlinx-datetime dependency is properly resolved
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Represents a user goal in the Eko app.
 * Goals can contain multiple tasks and have a timeframe.
 */
data class Goal(
    val id: String,
    val title: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val isCompleted: Boolean = false,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST
)

/**
 * Represents a specific task within a goal.
 * Tasks can be scheduled for specific time blocks.
 */
data class Task(
    val id: String,
    val goalId: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val createdAt: Instant = Instant.DISTANT_PAST,
    val updatedAt: Instant = Instant.DISTANT_PAST
)

/**
 * Priority levels for tasks.
 */
enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

/**
 * Represents a scheduled time block for working on a task.
 */
data class TimeBlock(
    val id: String,
    val taskId: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isCompleted: Boolean = false
)

/**
 * Represents the current status of Eko's focus.
 * This is what will be displayed in the UI as "Eko is focused on..."
 */
data class EkoStatus(
    val currentTask: Task?,
    val currentTimeBlock: TimeBlock?,
    val message: String
)

/**
 * Represents a reflection or motivation message from Eko (via Claude API).
 */
data class EkoReflection(
    val id: String,
    val message: String,
    val type: ReflectionType,
    val createdAt: Instant = Instant.DISTANT_PAST
)

/**
 * Types of reflections that Eko can provide.
 */
enum class ReflectionType {
    GOAL_ASSISTANCE,  // Breaking down a goal into steps
    NARRATIVE,        // What Eko is "really" up to
    MOTIVATION,       // Encouragement when stuck
    REFLECTION        // End-of-day reflection
}
