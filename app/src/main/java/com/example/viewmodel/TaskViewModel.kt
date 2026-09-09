package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DataStoreManager
import com.example.data.TaskEntity
import com.example.data.TaskRepository
import com.example.data.ThemeMode
import com.example.data.UserPreferences
import com.example.utils.DateTimeUtils
import com.example.utils.StorageUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

enum class TaskFilter {
    ALL,
    PENDING,
    COMPLETED
}

data class StorageStats(
    val databaseSizeBytes: Long = 0L,
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val backupsCount: Int = 0,
    val backupsTotalSizeBytes: Long = 0L
)

class TaskViewModel(
    private val repository: TaskRepository,
    private val dataStoreManager: DataStoreManager,
    private val appContext: Context
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val filterStatus = MutableStateFlow(TaskFilter.ALL)
    val focusedTaskId = MutableStateFlow<Long?>(null)

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    private val _storageStats = MutableStateFlow(StorageStats())
    val storageStats: StateFlow<StorageStats> = _storageStats.asStateFlow()

    private val _backupFiles = MutableStateFlow<List<File>>(emptyList())
    val backupFiles: StateFlow<List<File>> = _backupFiles.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = dataStoreManager.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val tasks: StateFlow<List<TaskEntity>> = combine(
        repository.allTasks,
        searchQuery,
        filterStatus
    ) { all, query, filter ->
        all.filter { task ->
            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.PENDING -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
            }
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Group tasks by Date Category Header: "Today", "Tomorrow", "Overdue", or formatted date
    val groupedTasks: StateFlow<Map<String, List<TaskEntity>>> = tasks.combine(searchQuery) { taskList, _ ->
        taskList.groupBy { task ->
            DateTimeUtils.getDateCategoryHeader(task.timestamp, task.isCompleted)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    init {
        refreshStorageStats()
        // Listen to counts to update storage stats
        viewModelScope.launch {
            combine(
                repository.totalCount,
                repository.completedCount,
                repository.pendingCount
            ) { total, completed, pending ->
                _storageStats.value = _storageStats.value.copy(
                    totalCount = total,
                    completedCount = completed,
                    pendingCount = pending
                )
            }.collect {}
        }
    }

    fun setFocusedTaskId(taskId: Long?) {
        focusedTaskId.value = taskId
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onFilterChange(newFilter: TaskFilter) {
        filterStatus.value = newFilter
    }

    fun addTask(
        title: String,
        description: String,
        timestamp: Long,
        reminderOffsetMinutes: Int,
        priority: String = "Medium"
    ) {
        viewModelScope.launch {
            try {
                val newTask = TaskEntity(
                    title = title.trim(),
                    description = description.trim(),
                    timestamp = timestamp,
                    reminderOffsetMinutes = reminderOffsetMinutes,
                    priority = priority
                )
                repository.insertTask(newTask)
                _uiMessage.emit("Task created and reminder scheduled!")
                refreshStorageStats()
            } catch (e: Exception) {
                _uiMessage.emit("Failed to create task: ${e.localizedMessage}")
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
                _uiMessage.emit("Task updated!")
                refreshStorageStats()
            } catch (e: Exception) {
                _uiMessage.emit("Failed to update task: ${e.localizedMessage}")
            }
        }
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            try {
                repository.toggleTaskCompleted(task)
                val statusText = if (!task.isCompleted) "completed" else "marked pending"
                _uiMessage.emit("Task $statusText")
                refreshStorageStats()
            } catch (e: Exception) {
                _uiMessage.emit("Error updating task: ${e.localizedMessage}")
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
                _uiMessage.emit("Task deleted")
                refreshStorageStats()
            } catch (e: Exception) {
                _uiMessage.emit("Failed to delete: ${e.localizedMessage}")
            }
        }
    }

    fun refreshStorageStats() {
        viewModelScope.launch {
            try {
                val dbSize = StorageUtils.getDatabaseSize(appContext)
                val backups = StorageUtils.getBackupFiles(appContext)
                val backupsTotalSize = StorageUtils.getBackupsTotalSize(appContext)
                _backupFiles.value = backups
                _storageStats.value = _storageStats.value.copy(
                    databaseSizeBytes = dbSize,
                    backupsCount = backups.size,
                    backupsTotalSizeBytes = backupsTotalSize
                )
            } catch (_: Exception) {}
        }
    }

    fun exportBackup() {
        viewModelScope.launch {
            try {
                val allTasks = repository.getAllTasksDirect()
                if (allTasks.isEmpty()) {
                    _uiMessage.emit("No tasks to export.")
                    return@launch
                }
                val exportedFile = StorageUtils.exportTasksToJson(appContext, allTasks)
                refreshStorageStats()
                _uiMessage.emit("Backup saved: ${exportedFile.name} (${allTasks.size} tasks)")
            } catch (e: Exception) {
                _uiMessage.emit("Export failed: ${e.localizedMessage}")
            }
        }
    }

    fun importBackupFromFile(file: File, replaceExisting: Boolean) {
        viewModelScope.launch {
            try {
                val importedTasks = StorageUtils.importTasksFromFile(file)
                if (importedTasks.isEmpty()) {
                    _uiMessage.emit("Backup file is empty or invalid.")
                    return@launch
                }
                repository.restoreTasks(importedTasks, replaceExisting)
                refreshStorageStats()
                val action = if (replaceExisting) "Replaced all tasks with" else "Restored"
                _uiMessage.emit("$action ${importedTasks.size} tasks from backup.")
            } catch (e: Exception) {
                _uiMessage.emit("Import failed: ${e.localizedMessage}")
            }
        }
    }

    fun importBackupFromStream(inputStream: InputStream, replaceExisting: Boolean) {
        viewModelScope.launch {
            try {
                val importedTasks = StorageUtils.importTasksFromInputStream(inputStream)
                if (importedTasks.isEmpty()) {
                    _uiMessage.emit("Selected file has no valid tasks.")
                    return@launch
                }
                repository.restoreTasks(importedTasks, replaceExisting)
                refreshStorageStats()
                val action = if (replaceExisting) "Replaced with" else "Restored"
                _uiMessage.emit("$action ${importedTasks.size} tasks from imported JSON.")
            } catch (e: Exception) {
                _uiMessage.emit("Import failed: ${e.localizedMessage}")
            }
        }
    }

    fun deleteBackup(file: File) {
        viewModelScope.launch {
            if (StorageUtils.deleteBackupFile(file)) {
                refreshStorageStats()
                _uiMessage.emit("Deleted backup file: ${file.name}")
            } else {
                _uiMessage.emit("Could not delete backup file.")
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            dataStoreManager.setThemeMode(mode)
        }
    }

    fun setDefaultAlarmOffset(offsetMinutes: Int) {
        viewModelScope.launch {
            dataStoreManager.setDefaultAlarmOffset(offsetMinutes)
        }
    }

    fun setNotificationSound(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setNotificationSoundEnabled(enabled)
        }
    }

    fun setNotificationVibrate(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setNotificationVibrateEnabled(enabled)
        }
    }
}

class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository, dataStoreManager, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
