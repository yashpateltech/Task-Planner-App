package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object NotificationHelper {

    const val CHANNEL_ID = "task_planner_channel"
    private const val CHANNEL_NAME = "Task Planner Reminders"
    private const val CHANNEL_DESC = "High priority notifications for scheduled tasks"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    lightColor = Color.CYAN
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun showTaskNotification(
        context: Context,
        taskId: Long,
        title: String,
        description: String,
        soundEnabled: Boolean = true,
        vibrateEnabled: Boolean = true
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to open MainActivity and highlight this task
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_TASK_ID, taskId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action Intent to Mark Task as Done directly from notification
        val doneIntent = Intent(context, TaskAlarmReceiver::class.java).apply {
            action = TaskAlarmReceiver.ACTION_MARK_DONE
            putExtra(TaskAlarmReceiver.EXTRA_TASK_ID, taskId)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(description.ifBlank { "Scheduled task reminder" })
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(if (description.isNotBlank()) "$description\nScheduled reminder" else "Time to get this task done!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.checkbox_on_background,
                "Mark as Done",
                donePendingIntent
            )

        if (soundEnabled) {
            builder.setSound(defaultSoundUri)
        }
        if (vibrateEnabled) {
            builder.setVibrate(longArrayOf(0, 350, 250, 350))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        notificationManager.notify(taskId.toInt(), builder.build())
    }

    fun cancelNotification(context: Context, taskId: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.toInt())
    }
}
