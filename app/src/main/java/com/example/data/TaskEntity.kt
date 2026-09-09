package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("isCompleted")
    val isCompleted: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @SerializedName("reminderOffsetMinutes")
    val reminderOffsetMinutes: Int = 0,

    @SerializedName("priority")
    val priority: String = "Medium"
)
