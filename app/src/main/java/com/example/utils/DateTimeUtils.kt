package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    private val fullDateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val standardDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return "${formatDate(timestamp)} at ${formatTime(timestamp)}"
    }

    fun getDateCategoryHeader(timestamp: Long, isCompleted: Boolean = false): String {
        val nowCal = Calendar.getInstance()
        val taskCal = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = nowCal.get(Calendar.YEAR) == taskCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.DAY_OF_YEAR) == taskCal.get(Calendar.DAY_OF_YEAR)

        nowCal.add(Calendar.DAY_OF_YEAR, 1)
        val isTomorrow = nowCal.get(Calendar.YEAR) == taskCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.DAY_OF_YEAR) == taskCal.get(Calendar.DAY_OF_YEAR)

        nowCal.add(Calendar.DAY_OF_YEAR, -1) // reset back to today

        return when {
            isToday -> "Today"
            isTomorrow -> "Tomorrow"
            taskCal.before(nowCal) && !isCompleted -> "Overdue"
            else -> fullDateFormat.format(Date(timestamp))
        }
    }

    fun getDateSortKey(timestamp: Long): String {
        return standardDateFormat.format(Date(timestamp))
    }

    fun combineDateAndTime(dateMillis: Long, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
