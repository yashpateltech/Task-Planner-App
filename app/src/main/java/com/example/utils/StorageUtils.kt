package com.example.utils

import android.content.Context
import com.example.data.TaskDatabase
import com.example.data.TaskEntity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StorageUtils {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private const val BACKUP_DIR_NAME = "backups"

    fun getBackupsDirectory(context: Context): File {
        val dir = File(context.filesDir, BACKUP_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun exportTasksToJson(context: Context, tasks: List<TaskEntity>): File {
        val backupDir = getBackupsDirectory(context)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val backupFile = File(backupDir, "task_backup_$timestamp.json")

        val jsonString = gson.toJson(tasks)
        FileOutputStream(backupFile).use { output ->
            output.write(jsonString.toByteArray(Charsets.UTF_8))
        }
        return backupFile
    }

    fun importTasksFromFile(file: File): List<TaskEntity> {
        return FileInputStream(file).use { input ->
            importTasksFromInputStream(input)
        }
    }

    fun importTasksFromInputStream(inputStream: InputStream): List<TaskEntity> {
        val jsonString = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val type = object : TypeToken<List<TaskEntity>>() {}.type
        return gson.fromJson(jsonString, type) ?: emptyList()
    }

    fun getDatabaseSize(context: Context): Long {
        var totalSize: Long = 0
        try {
            val dbFile = context.getDatabasePath(TaskDatabase.DATABASE_NAME)
            if (dbFile.exists()) {
                totalSize += dbFile.length()
            }
            val walFile = File(dbFile.path + "-wal")
            if (walFile.exists()) {
                totalSize += walFile.length()
            }
            val shmFile = File(dbFile.path + "-shm")
            if (shmFile.exists()) {
                totalSize += shmFile.length()
            }
        } catch (_: Exception) {}
        return totalSize
    }

    fun getBackupFiles(context: Context): List<File> {
        val backupDir = getBackupsDirectory(context)
        val files = backupDir.listFiles { file -> file.isFile && file.name.endsWith(".json") }
        return files?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    fun getBackupsTotalSize(context: Context): Long {
        return getBackupFiles(context).sumOf { it.length() }
    }

    fun deleteBackupFile(file: File): Boolean {
        return try {
            if (file.exists()) file.delete() else false
        } catch (_: Exception) {
            false
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0)
            else -> String.format(Locale.getDefault(), "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
