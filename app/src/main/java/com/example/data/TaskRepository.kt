package com.example.data

import android.content.Context
import com.example.alarm.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskRepository(
    private val taskDao: TaskDao,
    private val context: Context
) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()
    val completedTasks: Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    val totalCount: Flow<Int> = taskDao.getTotalCount()
    val completedCount: Flow<Int> = taskDao.getCompletedCount()
    val pendingCount: Flow<Int> = taskDao.getPendingCount()

    fun getTaskById(id: Long): Flow<TaskEntity?> = taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        val id = taskDao.insertTask(task)
        val savedTask = task.copy(id = id)
        AlarmScheduler.scheduleTaskAlarm(context, savedTask)
        id
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
        if (task.isCompleted) {
            AlarmScheduler.cancelTaskAlarm(context, task.id)
        } else {
            AlarmScheduler.scheduleTaskAlarm(context, task)
        }
    }

    suspend fun toggleTaskCompleted(task: TaskEntity) = withContext(Dispatchers.IO) {
        val newStatus = !task.isCompleted
        taskDao.setTaskCompleted(task.id, newStatus)
        if (newStatus) {
            AlarmScheduler.cancelTaskAlarm(context, task.id)
        } else {
            val updated = task.copy(isCompleted = false)
            AlarmScheduler.scheduleTaskAlarm(context, updated)
        }
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
        AlarmScheduler.cancelTaskAlarm(context, task.id)
    }

    suspend fun deleteTaskById(id: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(id)
        AlarmScheduler.cancelTaskAlarm(context, id)
    }

    suspend fun getAllTasksDirect(): List<TaskEntity> = withContext(Dispatchers.IO) {
        taskDao.getAllTasksDirect()
    }

    suspend fun restoreTasks(tasks: List<TaskEntity>, replaceExisting: Boolean) = withContext(Dispatchers.IO) {
        if (replaceExisting) {
            // Cancel existing alarms
            val current = taskDao.getAllTasksDirect()
            for (t in current) {
                AlarmScheduler.cancelTaskAlarm(context, t.id)
            }
            taskDao.clearAll()
        }
        taskDao.insertAll(tasks)

        // Reschedule alarms for restored pending tasks
        val upcoming = taskDao.getUpcomingPendingTasks(System.currentTimeMillis())
        for (task in upcoming) {
            AlarmScheduler.scheduleTaskAlarm(context, task)
        }
    }
}
