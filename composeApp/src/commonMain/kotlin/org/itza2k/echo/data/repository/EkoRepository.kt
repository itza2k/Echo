package org.itza2k.echo.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.data.db.EkoDatabase
import org.itza2k.echo.data.model.Goal
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.TaskPriority
import org.itza2k.echo.data.model.TimeBlock
import java.util.UUID

/**
 * Repository for interacting with the Eko database.
 */
class EkoRepository(private val database: EkoDatabase) {
    
    // Current time zone
    private val timeZone = TimeZone.currentSystemDefault()
    
    // Generate a unique ID
    private fun generateId(): String = UUID.randomUUID().toString()
    
    // Get current timestamp
    private fun getCurrentTimestamp(): Instant = Clock.System.now()
    
    // Convert LocalDate to string
    private fun LocalDate.toIsoString(): String = toString()
    
    // Convert LocalTime to string
    private fun LocalTime.toIsoString(): String = toString()
    
    // Convert string to LocalDate
    private fun String.toLocalDate(): LocalDate = LocalDate.parse(this)
    
    // Convert string to LocalTime
    private fun String.toLocalTime(): LocalTime = LocalTime.parse(this)
    
    // Convert string to Instant
    private fun String.toInstant(): Instant = Instant.parse(this)
    
    // Convert database Goal to model Goal
    private fun org.itza2k.echo.data.db.Goal.toModel(): Goal {
        return Goal(
            id = id,
            title = title,
            description = description,
            startDate = start_date.toLocalDate(),
            endDate = end_date?.toLocalDate(),
            isCompleted = is_completed,
            createdAt = created_at.toInstant(),
            updatedAt = updated_at.toInstant()
        )
    }
    
    // Convert database Task to model Task
    private fun org.itza2k.echo.data.db.Task.toModel(): Task {
        return Task(
            id = id,
            goalId = goal_id,
            title = title,
            description = description,
            isCompleted = is_completed,
            priority = TaskPriority.valueOf(priority),
            createdAt = created_at.toInstant(),
            updatedAt = updated_at.toInstant()
        )
    }
    
    // Convert database TimeBlock to model TimeBlock
    private fun org.itza2k.echo.data.db.TimeBlock.toModel(): TimeBlock {
        return TimeBlock(
            id = id,
            taskId = task_id,
            date = date.toLocalDate(),
            startTime = start_time.toLocalTime(),
            endTime = end_time.toLocalTime(),
            isCompleted = is_completed
        )
    }
    
    // Goals
    
    /**
     * Get all goals as a Flow.
     */
    fun getAllGoals(): Flow<List<Goal>> {
        return database.goalQueries.getAllGoals()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { goals -> goals.map { it.toModel() } }
    }
    
    /**
     * Insert a new goal.
     */
    suspend fun insertGoal(
        title: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): String = withContext(Dispatchers.Default) {
        val id = generateId()
        val now = getCurrentTimestamp().toString()
        
        database.goalQueries.insertGoal(
            id = id,
            title = title,
            description = description,
            start_date = startDate.toIsoString(),
            end_date = endDate?.toIsoString(),
            is_completed = false,
            created_at = now,
            updated_at = now
        )
        
        return@withContext id
    }
    
    // Tasks
    
    /**
     * Get all tasks as a Flow.
     */
    fun getAllTasks(): Flow<List<Task>> {
        return database.taskQueries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tasks -> tasks.map { it.toModel() } }
    }
    
    /**
     * Get all tasks for a specific goal.
     */
    fun getTasksByGoalId(goalId: String): Flow<List<Task>> {
        return database.taskQueries.getTasksByGoalId(goalId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { tasks -> tasks.map { it.toModel() } }
    }
    
    /**
     * Insert a new task.
     */
    suspend fun insertTask(
        goalId: String,
        title: String,
        description: String,
        priority: TaskPriority = TaskPriority.MEDIUM
    ): String = withContext(Dispatchers.Default) {
        val id = generateId()
        val now = getCurrentTimestamp().toString()
        
        database.taskQueries.insertTask(
            id = id,
            goal_id = goalId,
            title = title,
            description = description,
            is_completed = false,
            priority = priority.name,
            created_at = now,
            updated_at = now
        )
        
        return@withContext id
    }
    
    /**
     * Mark a task as completed.
     */
    suspend fun markTaskCompleted(id: String) = withContext(Dispatchers.Default) {
        val now = getCurrentTimestamp().toString()
        database.taskQueries.markTaskCompleted(updated_at = now, id = id)
    }
    
    // Time Blocks
    
    /**
     * Get all time blocks for today.
     */
    fun getTodayTimeBlocks(): Flow<List<TimeBlock>> {
        val today = Clock.System.now().toLocalDateTime(timeZone).date.toIsoString()
        return database.timeBlockQueries.getTodayTimeBlocks(today)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { timeBlocks -> timeBlocks.map { it.toModel() } }
    }
    
    /**
     * Insert a new time block.
     */
    suspend fun insertTimeBlock(
        taskId: String,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): String = withContext(Dispatchers.Default) {
        val id = generateId()
        
        database.timeBlockQueries.insertTimeBlock(
            id = id,
            task_id = taskId,
            date = date.toIsoString(),
            start_time = startTime.toIsoString(),
            end_time = endTime.toIsoString(),
            is_completed = false
        )
        
        return@withContext id
    }
}