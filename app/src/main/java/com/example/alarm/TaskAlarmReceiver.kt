package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.DataStoreManager
import com.example.data.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MARK_DONE = "com.example.ACTION_MARK_DONE"
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            AlarmScheduler.ACTION_TASK_REMINDER -> {
                val taskId = intent.getLongExtra(AlarmScheduler.EXTRA_TASK_ID, -1L)
                val title = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_TITLE) ?: "Task Reminder"
                val description = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_DESC) ?: ""

                if (taskId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Check if task is already completed
                            val database = TaskDatabase.getInstance(context)
                            val task = database.taskDao().getTaskByIdDirect(taskId)
                            if (task != null && !task.isCompleted) {
                                val dataStoreManager = DataStoreManager(context)
                                val prefs = dataStoreManager.userPreferencesFlow.first()
                                NotificationHelper.showTaskNotification(
                                    context = context,
                                    taskId = taskId,
                                    title = title,
                                    description = description,
                                    soundEnabled = prefs.notificationSoundEnabled,
                                    vibrateEnabled = prefs.notificationVibrateEnabled
                                )
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }

            ACTION_MARK_DONE -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                if (taskId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val database = TaskDatabase.getInstance(context)
                            database.taskDao().setTaskCompleted(taskId, true)
                            NotificationHelper.cancelNotification(context, taskId)
                            AlarmScheduler.cancelTaskAlarm(context, taskId)
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = TaskDatabase.getInstance(context)
                        val upcomingTasks = database.taskDao().getUpcomingPendingTasks(System.currentTimeMillis())
                        for (task in upcomingTasks) {
                            AlarmScheduler.scheduleTaskAlarm(context, task)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
