package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TaskEntity
import com.example.utils.StorageUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Task Planner", appName)
  }

  @Test
  fun `test storage export and import json roundtrip`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sampleTasks = listOf(
      TaskEntity(
        id = 1L,
        title = "Sprint Planning",
        description = "Review Q4 roadmap",
        timestamp = System.currentTimeMillis() + 3600000L,
        isCompleted = false,
        reminderOffsetMinutes = 10,
        priority = "High"
      ),
      TaskEntity(
        id = 2L,
        title = "Grocery Shopping",
        description = "Milk, fruits, vegetables",
        timestamp = System.currentTimeMillis() + 7200000L,
        isCompleted = true,
        reminderOffsetMinutes = 0,
        priority = "Low"
      )
    )

    val backupFile = StorageUtils.exportTasksToJson(context, sampleTasks)
    assertTrue(backupFile.exists())
    assertTrue(backupFile.length() > 0)

    val imported = StorageUtils.importTasksFromFile(backupFile)
    assertEquals(2, imported.size)
    assertEquals("Sprint Planning", imported[0].title)
    assertEquals("Grocery Shopping", imported[1].title)
    assertTrue(imported[1].isCompleted)

    StorageUtils.deleteBackupFile(backupFile)
  }
}
