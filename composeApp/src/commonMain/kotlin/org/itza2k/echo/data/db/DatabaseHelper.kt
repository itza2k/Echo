package org.itza2k.echo.data.db

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.itza2k.echo.data.model.Goal
import org.itza2k.echo.data.model.MoodEnergyEntry
import org.itza2k.echo.data.model.MoodLevel
import org.itza2k.echo.data.model.EnergyLevel
import org.itza2k.echo.data.model.Task
import org.itza2k.echo.data.model.TimeBlock
import org.itza2k.echo.data.model.TaskPriority
import org.itza2k.echo.data.model.EkoReflection
import org.itza2k.echo.data.model.ReflectionType
import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.data.db.Goal as DbGoal
import org.itza2k.echo.data.db.Task as DbTask
import org.itza2k.echo.data.db.TimeBlock as DbTimeBlock
import org.itza2k.echo.data.db.ApiKey as DbApiKey

/**
 * Helper class for database operations.
 * This is a singleton to ensure we only have one instance of the database.
 */
class DatabaseHelper private constructor(sqlDriver: SqlDriver) {

    companion object {
        private var instance: DatabaseHelper? = null
        private const val DATABASE_NAME = "eko.db"

        fun getInstance(sqlDriver: SqlDriver): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(sqlDriver).also { instance = it }
            }
        }

        /**
         * Get an instance of DatabaseHelper using the platform-specific DriverFactory.
         * This is the preferred way to get a DatabaseHelper instance.
         */
        fun getInstance(): DatabaseHelper {
            val driverFactory = createPlatformDriverFactory()
            val sqlDriver = driverFactory.createDriver(DATABASE_NAME)
            return getInstance(sqlDriver)
        }
    }

    // Create the database
    private val database = EkoDatabase(
        driver = sqlDriver,
        GoalAdapter = DbGoal.Adapter(
            is_completedAdapter = BooleanAdapter
        ),
        TaskAdapter = DbTask.Adapter(
            is_completedAdapter = BooleanAdapter
        ),
        TimeBlockAdapter = DbTimeBlock.Adapter(
            is_completedAdapter = BooleanAdapter
        )
    )

    // State flows for the data
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _timeBlocks = MutableStateFlow<List<TimeBlock>>(emptyList())
    val timeBlocks: StateFlow<List<TimeBlock>> = _timeBlocks.asStateFlow()

    private val _moodEnergyEntries = MutableStateFlow<List<MoodEnergyEntry>>(emptyList())
    val moodEnergyEntries: StateFlow<List<MoodEnergyEntry>> = _moodEnergyEntries.asStateFlow()

    private val _reflections = MutableStateFlow<List<EkoReflection>>(emptyList())
    val reflections: StateFlow<List<EkoReflection>> = _reflections.asStateFlow()

    /**
     * Initialize the database with sample data if it's empty.
     */
    suspend fun initializeDatabase() {
        // Check if we have any goals
        val goals = database.goalQueries.getAllGoals().executeAsList()

        if (goals.isEmpty()) {
            // Create a sample goal
            val goalId = createSampleGoal()

            // Create sample tasks for the goal
            createSampleTasks(goalId)
        }

        // Load the data
        loadData()
    }

    /**
     * Load all data from the database.
     */
    private suspend fun loadData() {
        // Load goals
        val goalsList = database.goalQueries.getAllGoals().executeAsList().map { dbGoal ->
            Goal(
                id = dbGoal.id,
                title = dbGoal.title,
                description = dbGoal.description,
                startDate = LocalDate.parse(dbGoal.start_date),
                endDate = dbGoal.end_date?.let { LocalDate.parse(it) },
                isCompleted = dbGoal.is_completed,
                createdAt = Instant.parse(dbGoal.created_at),
                updatedAt = Instant.parse(dbGoal.updated_at)
            )
        }
        _goals.value = goalsList

        // Load tasks
        val tasksList = database.taskQueries.getAllTasks().executeAsList().map { dbTask ->
            Task(
                id = dbTask.id,
                goalId = dbTask.goal_id,
                title = dbTask.title,
                description = dbTask.description,
                isCompleted = dbTask.is_completed,
                priority = org.itza2k.echo.data.model.TaskPriority.valueOf(dbTask.priority),
                createdAt = Instant.parse(dbTask.created_at),
                updatedAt = Instant.parse(dbTask.updated_at)
            )
        }
        _tasks.value = tasksList

        // Load time blocks
        val timeBlocksList = database.timeBlockQueries.getAllTimeBlocks().executeAsList().map { dbTimeBlock ->
            TimeBlock(
                id = dbTimeBlock.id,
                taskId = dbTimeBlock.task_id,
                date = LocalDate.parse(dbTimeBlock.date),
                startTime = kotlinx.datetime.LocalTime.parse(dbTimeBlock.start_time),
                endTime = kotlinx.datetime.LocalTime.parse(dbTimeBlock.end_time),
                isCompleted = dbTimeBlock.is_completed
            )
        }
        _timeBlocks.value = timeBlocksList

        // Load mood/energy entries
        loadMoodEnergyEntries()
    }

    /**
     * Create a sample goal.
     */
    private fun createSampleGoal(): String {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val goalId = UUID.randomUUID().toString()

        database.goalQueries.insertGoal(
            id = goalId,
            title = "Complete Project Proposal",
            description = "Finish the project proposal for the client meeting",
            start_date = today.toString(),
            end_date = null,
            is_completed = false,
            created_at = now.toString(),
            updated_at = now.toString()
        )

        return goalId
    }

    /**
     * Create sample tasks for a goal.
     */
    private fun createSampleTasks(goalId: String) {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        // Task 1: Research competitors
        val task1Id = UUID.randomUUID().toString()
        database.taskQueries.insertTask(
            id = task1Id,
            goal_id = goalId,
            title = "Research competitors",
            description = "Analyze competitor products and features",
            is_completed = true,
            priority = org.itza2k.echo.data.model.TaskPriority.HIGH.name,
            created_at = now.toString(),
            updated_at = now.toString()
        )

        // Task 2: Draft project outline
        val task2Id = UUID.randomUUID().toString()
        database.taskQueries.insertTask(
            id = task2Id,
            goal_id = goalId,
            title = "Draft project outline",
            description = "Create a detailed outline of the project scope",
            is_completed = false,
            priority = org.itza2k.echo.data.model.TaskPriority.HIGH.name,
            created_at = now.toString(),
            updated_at = now.toString()
        )

        // Task 3: Create mockups
        val task3Id = UUID.randomUUID().toString()
        database.taskQueries.insertTask(
            id = task3Id,
            goal_id = goalId,
            title = "Create mockups",
            description = "Design initial mockups for the client",
            is_completed = false,
            priority = org.itza2k.echo.data.model.TaskPriority.MEDIUM.name,
            created_at = now.toString(),
            updated_at = now.toString()
        )

        // Create time blocks for the tasks
        createSampleTimeBlocks(task1Id, task2Id, task3Id, today)
    }

    /**
     * Create sample time blocks for tasks.
     */
    private fun createSampleTimeBlocks(task1Id: String, task2Id: String, task3Id: String, today: LocalDate) {
        // Time block for task 1
        database.timeBlockQueries.insertTimeBlock(
            id = UUID.randomUUID().toString(),
            task_id = task1Id,
            date = today.toString(),
            start_time = "09:00",
            end_time = "10:30",
            is_completed = true
        )

        // Time block for task 2
        database.timeBlockQueries.insertTimeBlock(
            id = UUID.randomUUID().toString(),
            task_id = task2Id,
            date = today.toString(),
            start_time = "11:00",
            end_time = "12:30",
            is_completed = false
        )

        // Time block for task 3
        database.timeBlockQueries.insertTimeBlock(
            id = UUID.randomUUID().toString(),
            task_id = task3Id,
            date = today.toString(),
            start_time = "14:00",
            end_time = "16:00",
            is_completed = false
        )
    }

    /**
     * Add a new task.
     */
    suspend fun addTask(task: Task) {
        database.taskQueries.insertTask(
            id = task.id,
            goal_id = task.goalId,
            title = task.title,
            description = task.description,
            is_completed = task.isCompleted,
            priority = task.priority.name,
            created_at = task.createdAt.toString(),
            updated_at = task.updatedAt.toString()
        )

        // Reload the data
        loadData()
    }

    /**
     * Update a task's completion status.
     */
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        val now = Clock.System.now().toString()

        if (isCompleted) {
            database.taskQueries.markTaskCompleted(updated_at = now, id = taskId)
        } else {
            database.taskQueries.markTaskIncomplete(updated_at = now, id = taskId)
        }

        // Reload the data
        loadData()
    }

    /**
     * Add a new goal.
     */
    suspend fun addGoal(goal: Goal) {
        database.goalQueries.insertGoal(
            id = goal.id,
            title = goal.title,
            description = goal.description,
            start_date = goal.startDate.toString(),
            end_date = goal.endDate?.toString(),
            is_completed = goal.isCompleted,
            created_at = goal.createdAt.toString(),
            updated_at = goal.updatedAt.toString()
        )

        // Reload the data
        loadData()
    }

    /**
     * Update an existing goal.
     */
    suspend fun updateGoal(goal: Goal) {
        database.goalQueries.updateGoal(
            title = goal.title,
            description = goal.description,
            start_date = goal.startDate.toString(),
            end_date = goal.endDate?.toString(),
            is_completed = goal.isCompleted,
            updated_at = goal.updatedAt.toString(),
            id = goal.id
        )

        // Reload the data
        loadData()
    }

    /**
     * Delete a goal.
     */
    suspend fun deleteGoal(goalId: String) {
        database.goalQueries.deleteGoal(id = goalId)

        // Reload the data
        loadData()
    }

    /**
     * Update a goal's completion status.
     */
    suspend fun updateGoalCompletion(goalId: String, isCompleted: Boolean) {
        val now = Clock.System.now().toString()

        if (isCompleted) {
            database.goalQueries.markGoalCompleted(updated_at = now, id = goalId)
        } else {
            // Get the current goal
            val goal = database.goalQueries.getGoalById(id = goalId).executeAsOne()

            // Update the goal with is_completed = false
            database.goalQueries.updateGoal(
                title = goal.title,
                description = goal.description,
                start_date = goal.start_date,
                end_date = goal.end_date,
                is_completed = false,
                updated_at = now,
                id = goalId
            )
        }

        // Reload the data
        loadData()
    }

    /**
     * Add a new time block.
     */
    suspend fun addTimeBlock(timeBlock: TimeBlock) {
        database.timeBlockQueries.insertTimeBlock(
            id = timeBlock.id,
            task_id = timeBlock.taskId,
            date = timeBlock.date.toString(),
            start_time = timeBlock.startTime.toString(),
            end_time = timeBlock.endTime.toString(),
            is_completed = timeBlock.isCompleted
        )

        // Reload the data
        loadData()
    }

    /**
     * Update an existing time block.
     */
    suspend fun updateTimeBlock(timeBlock: TimeBlock) {
        database.timeBlockQueries.updateTimeBlock(
            task_id = timeBlock.taskId,
            date = timeBlock.date.toString(),
            start_time = timeBlock.startTime.toString(),
            end_time = timeBlock.endTime.toString(),
            is_completed = timeBlock.isCompleted,
            id = timeBlock.id
        )

        // Reload the data
        loadData()
    }

    /**
     * Delete a time block.
     */
    suspend fun deleteTimeBlock(timeBlockId: String) {
        database.timeBlockQueries.deleteTimeBlock(id = timeBlockId)

        // Reload the data
        loadData()
    }

    /**
     * Update a time block's completion status.
     */
    suspend fun updateTimeBlockCompletion(timeBlockId: String, isCompleted: Boolean) {
        if (isCompleted) {
            database.timeBlockQueries.markTimeBlockCompleted(id = timeBlockId)
        } else {
            database.timeBlockQueries.markTimeBlockIncomplete(id = timeBlockId)
        }

        // Reload the data
        loadData()
    }

    /**
     * Add a new mood/energy entry.
     */
    suspend fun addMoodEnergyEntry(entry: MoodEnergyEntry) {
        database.moodEnergyEntryQueries.insertMoodEnergyEntry(
            id = entry.id,
            mood_level = entry.moodLevel.name,
            energy_level = entry.energyLevel.name,
            note = entry.note,
            date = entry.date.toString(),
            time = entry.time.toString(),
            created_at = entry.createdAt.toString()
        )

        // Load mood/energy entries
        loadMoodEnergyEntries()
    }

    /**
     * Load all mood/energy entries from the database.
     */
    private suspend fun loadMoodEnergyEntries() {
        val entries = database.moodEnergyEntryQueries.getAllMoodEnergyEntries().executeAsList().map { dbEntry ->
            MoodEnergyEntry(
                id = dbEntry.id,
                moodLevel = MoodLevel.valueOf(dbEntry.mood_level),
                energyLevel = EnergyLevel.valueOf(dbEntry.energy_level),
                note = dbEntry.note,
                date = LocalDate.parse(dbEntry.date),
                time = kotlinx.datetime.LocalTime.parse(dbEntry.time),
                createdAt = Instant.parse(dbEntry.created_at)
            )
        }
        _moodEnergyEntries.value = entries
    }

    /**
     * Get mood/energy entries for a specific date.
     */
    suspend fun getMoodEnergyEntriesByDate(date: LocalDate): List<MoodEnergyEntry> {
        val entries = database.moodEnergyEntryQueries.getMoodEnergyEntriesByDate(date.toString()).executeAsList().map { dbEntry ->
            MoodEnergyEntry(
                id = dbEntry.id,
                moodLevel = MoodLevel.valueOf(dbEntry.mood_level),
                energyLevel = EnergyLevel.valueOf(dbEntry.energy_level),
                note = dbEntry.note,
                date = LocalDate.parse(dbEntry.date),
                time = kotlinx.datetime.LocalTime.parse(dbEntry.time),
                createdAt = Instant.parse(dbEntry.created_at)
            )
        }
        return entries
    }

    /**
     * Add a new reflection.
     */
    suspend fun addReflection(reflection: EkoReflection) {
        // For simplicity, we'll store reflections in memory only for now
        // In a real implementation, we would add a table for reflections in the database
        val currentReflections = _reflections.value.toMutableList()
        currentReflections.add(reflection)
        _reflections.value = currentReflections
    }

    /**
     * Get all reflections.
     */
    fun getAllReflections(): List<EkoReflection> {
        return _reflections.value
    }

    /**
     * Get reflections by type.
     */
    fun getReflectionsByType(type: ReflectionType): List<EkoReflection> {
        return _reflections.value.filter { it.type == type }
    }
}

/**
 * Adapter for converting between Boolean and Long in SQLDelight.
 */
object BooleanAdapter : app.cash.sqldelight.ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}
