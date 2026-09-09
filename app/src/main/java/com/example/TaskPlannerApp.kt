package com.example

import android.app.Application
import com.example.alarm.NotificationHelper
import com.example.data.DataStoreManager
import com.example.data.TaskDatabase
import com.example.data.TaskRepository

class TaskPlannerApp : Application() {

    val database by lazy { TaskDatabase.getInstance(this) }
    val dataStoreManager by lazy { DataStoreManager(this) }
    val repository by lazy { TaskRepository(database.taskDao(), this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this)
    }
}
