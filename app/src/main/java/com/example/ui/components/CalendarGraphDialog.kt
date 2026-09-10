package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TaskEntity
import com.example.ui.theme.DarkBlackMutedText
import com.example.ui.theme.DarkBlackText
import com.example.ui.theme.DarkBlackVariantText
import com.example.ui.theme.PinkGradients
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.WhiteBackground
import com.example.utils.DateTimeUtils
import java.util.Calendar
import java.util.Date
import kotlin.math.max

enum class CalendarDialogTab {
    DAILY_GRAPH,
    CALENDAR_VIEW
}

data class DayStats(
    val dateMillis: Long,
    val dayLabel: String,
    val dateLabel: String,
    val completedCount: Int,
    val pendingCount: Int,
    val totalCount: Int,
    val isToday: Boolean,
    val isSelected: Boolean
)

@Composable
fun CalendarGraphDialog(
    tasks: List<TaskEntity>,
    onDismiss: () -> Unit,
    onToggleTaskCompleted: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(CalendarDialogTab.DAILY_GRAPH) }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    // Month calendar navigation state
    val calendarMonth = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    var currentMonthKey by remember { mutableStateOf(calendarMonth.timeInMillis) }

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("calendar_graph_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = if (isDark) MaterialTheme.colorScheme.surface else WhiteBackground,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Banner with Pink Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PinkGradients.Header)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Daily Tasks & Analytics",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Track daily completed vs pending progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { onDismiss() }
                                .testTag("close_calendar_dialog_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Tab Switcher Row (Graph vs Calendar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val graphTabSelected = selectedTab == CalendarDialogTab.DAILY_GRAPH
                    val calendarTabSelected = selectedTab == CalendarDialogTab.CALENDAR_VIEW

                    val activeTabBg = if (isDark) PinkPrimary.copy(alpha = 0.22f) else Color(0xFFFDE8EF)
                    val inactiveTabBg = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFFF9FB)
                    val inactiveTabBorder = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFF6E2EA)
                    val inactiveTabText = if (isDark) Color(0xFFCBD5E1) else Color(0xFF6B4E59)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (graphTabSelected) activeTabBg else inactiveTabBg,
                        border = BorderStroke(
                            1.dp,
                            if (graphTabSelected) PinkPrimary else inactiveTabBorder
                        ),
                        onClick = { selectedTab = CalendarDialogTab.DAILY_GRAPH },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("tab_daily_graph")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = if (graphTabSelected) PinkPrimary else inactiveTabText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Graph",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (graphTabSelected) PinkPrimary else inactiveTabText
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (calendarTabSelected) activeTabBg else inactiveTabBg,
                        border = BorderStroke(
                            1.dp,
                            if (calendarTabSelected) PinkPrimary else inactiveTabBorder
                        ),
                        onClick = { selectedTab = CalendarDialogTab.CALENDAR_VIEW },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("tab_calendar_view")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (calendarTabSelected) PinkPrimary else inactiveTabText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Calendar Matrix",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (calendarTabSelected) PinkPrimary else inactiveTabText
                            )
                        }
                    }
                }

                // Content View based on Selected Tab
                when (selectedTab) {
                    CalendarDialogTab.DAILY_GRAPH -> {
                        DailyGraphSection(
                            tasks = tasks,
                            selectedDateMillis = selectedDateMillis,
                            onSelectDate = { selectedDateMillis = it }
                        )
                    }
                    CalendarDialogTab.CALENDAR_VIEW -> {
                        MonthlyCalendarSection(
                            tasks = tasks,
                            calendarMonth = calendarMonth,
                            currentMonthKey = currentMonthKey,
                            onMonthChange = { currentMonthKey = it },
                            selectedDateMillis = selectedDateMillis,
                            onSelectDate = { selectedDateMillis = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tasks breakdown for the selected day
                SelectedDayTasksCard(
                    tasks = tasks,
                    selectedDateMillis = selectedDateMillis,
                    onToggleTaskCompleted = onToggleTaskCompleted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Requested Footer: "Made with love for you"
                MadeWithLoveFooter()

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun DailyGraphSection(
    tasks: List<TaskEntity>,
    selectedDateMillis: Long,
    onSelectDate: (Long) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    // Generate 7-day stats (from 3 days ago to 3 days ahead, or past 7 days)
    val dayStatsList = remember(tasks, selectedDateMillis) {
        val list = mutableListOf<DayStats>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -3) // start 3 days ago

        for (i in 0..6) {
            val dayStart = DateTimeUtils.getStartOfDay(cal.timeInMillis)
            val dayEnd = dayStart + 86_400_000L - 1L

            val tasksOnDay = tasks.filter { it.timestamp in dayStart..dayEnd }
            val completed = tasksOnDay.count { it.isCompleted }
            val pending = tasksOnDay.count { !it.isCompleted }

            val isToday = DateTimeUtils.isSameDay(cal.timeInMillis, System.currentTimeMillis())
            val isSelected = DateTimeUtils.isSameDay(cal.timeInMillis, selectedDateMillis)

            list.add(
                DayStats(
                    dateMillis = cal.timeInMillis,
                    dayLabel = DateTimeUtils.getDayAbbreviation(cal.timeInMillis),
                    dateLabel = "${DateTimeUtils.getDayOfMonth(cal.timeInMillis)}",
                    completedCount = completed,
                    pendingCount = pending,
                    totalCount = tasksOnDay.size,
                    isToday = isToday,
                    isSelected = isSelected
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val maxCount = remember(dayStatsList) {
        max(1, dayStatsList.maxOfOrNull { it.totalCount } ?: 1)
    }

    val totalCompletedInPeriod = dayStatsList.sumOf { it.completedCount }
    val totalPendingInPeriod = dayStatsList.sumOf { it.pendingCount }
    val totalInPeriod = totalCompletedInPeriod + totalPendingInPeriod
    val completionPercentage = if (totalInPeriod > 0) {
        (totalCompletedInPeriod * 100) / totalInPeriod
    } else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // High-level period summary metric row
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFFFF9FB)
            ),
            border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "7-Day Task Completion Rate",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else DarkBlackText
                    )
                    Text(
                        text = "$completionPercentage%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PinkPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Visual Gradient Progress Bar
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) Color(0xFF2E2E38) else Color(0xFFF6E2EA),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(if (totalInPeriod > 0) (completionPercentage / 100f).coerceIn(0.02f, 1f) else 0f)
                                .background(PinkGradients.Primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = PinkPrimary,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalCompletedInPeriod Completed",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) Color(0xFFE2E8F0) else DarkBlackText
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalPendingInPeriod Pending",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = if (isDark) Color(0xFFE2E8F0) else DarkBlackText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Dual-Bar Column Graph with Labels
        Text(
            text = "Daily Completed vs Pending Graph",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) Color.White else DarkBlackText
        )
        Text(
            text = "Tap any day to inspect and manage its tasks",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B4E59)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Graph Container
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
            ),
            border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Dual Bar Chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dayStatsList.forEach { day ->
                        val completedFraction = (day.completedCount.toFloat() / maxCount).coerceIn(0f, 1f)
                        val pendingFraction = (day.pendingCount.toFloat() / maxCount).coerceIn(0f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectDate(day.dateMillis) }
                                .padding(horizontal = 2.dp)
                        ) {
                            // Column Bars
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Completed Bar (Pink gradient)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    if (day.completedCount > 0) {
                                        Text(
                                            text = "${day.completedCount}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PinkPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(
                                                if (day.completedCount > 0) (80 * completedFraction).coerceAtLeast(8f).dp else 4.dp
                                            )
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                if (day.completedCount > 0) PinkGradients.Primary
                                                else Brush.verticalGradient(
                                                    listOf(
                                                        if (isDark) Color(0xFF2D1822) else Color(0xFFFDE8EF),
                                                        if (isDark) Color(0xFF2D1822) else Color(0xFFFDE8EF)
                                                    )
                                                )
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Pending Bar (Amber)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    if (day.pendingCount > 0) {
                                        Text(
                                            text = "${day.pendingCount}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF59E0B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(
                                                if (day.pendingCount > 0) (80 * pendingFraction).coerceAtLeast(8f).dp else 4.dp
                                            )
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                if (day.pendingCount > 0)
                                                    Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFF59E0B)))
                                                else
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            if (isDark) Color(0xFF2E2218) else Color(0xFFFCE7F3),
                                                            if (isDark) Color(0xFF2E2218) else Color(0xFFFCE7F3)
                                                        )
                                                    )
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Day Label Container
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    day.isSelected -> PinkPrimary
                                    day.isToday -> if (isDark) PinkPrimary.copy(alpha = 0.25f) else Color(0xFFFDE8EF)
                                    else -> Color.Transparent
                                },
                                border = if (day.isToday && !day.isSelected) BorderStroke(1.dp, PinkPrimary) else null,
                                modifier = Modifier.padding(horizontal = 1.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = day.dayLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        fontSize = 10.sp,
                                        color = if (day.isSelected) Color.White else (if (isDark) Color.White else DarkBlackText)
                                    )
                                    Text(
                                        text = day.dateLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = if (day.isSelected) Color.White.copy(alpha = 0.9f) else (if (isDark) Color(0xFF94A3B8) else DarkBlackVariantText)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF))
                Spacer(modifier = Modifier.height(8.dp))

                // Chart Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PinkGradients.Primary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) Color(0xFFE2E8F0) else DarkBlackText
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFF59E0B))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDark) Color(0xFFE2E8F0) else DarkBlackText
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyCalendarSection(
    tasks: List<TaskEntity>,
    calendarMonth: Calendar,
    currentMonthKey: Long,
    onMonthChange: (Long) -> Unit,
    selectedDateMillis: Long,
    onSelectDate: (Long) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val monthTitle = remember(currentMonthKey) {
        DateTimeUtils.getMonthYearTitle(calendarMonth)
    }

    // Days matrix generation for the displayed month
    val daysInMatrix = remember(currentMonthKey, tasks) {
        val days = mutableListOf<DayStats?>()
        val tempCal = Calendar.getInstance().apply {
            timeInMillis = calendarMonth.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }

        // Sunday = 1, Saturday = 7
        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
        val leadingEmptyCells = firstDayOfWeek - Calendar.SUNDAY

        // Add empty slots for days before the 1st
        for (i in 0 until leadingEmptyCells) {
            days.add(null)
        }

        val maxDaysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (d in 1..maxDaysInMonth) {
            tempCal.set(Calendar.DAY_OF_MONTH, d)
            val dayStart = DateTimeUtils.getStartOfDay(tempCal.timeInMillis)
            val dayEnd = dayStart + 86_400_000L - 1L

            val tasksOnDay = tasks.filter { it.timestamp in dayStart..dayEnd }
            val completed = tasksOnDay.count { it.isCompleted }
            val pending = tasksOnDay.count { !it.isCompleted }

            val isToday = DateTimeUtils.isSameDay(tempCal.timeInMillis, System.currentTimeMillis())
            val isSelected = DateTimeUtils.isSameDay(tempCal.timeInMillis, selectedDateMillis)

            days.add(
                DayStats(
                    dateMillis = tempCal.timeInMillis,
                    dayLabel = DateTimeUtils.getDayAbbreviation(tempCal.timeInMillis),
                    dateLabel = "$d",
                    completedCount = completed,
                    pendingCount = pending,
                    totalCount = tasksOnDay.size,
                    isToday = isToday,
                    isSelected = isSelected
                )
            )
        }
        days
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Month Navigation Row
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
            ),
            border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            calendarMonth.add(Calendar.MONTH, -1)
                            onMonthChange(calendarMonth.timeInMillis)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = PinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else DarkBlackText
                    )

                    IconButton(
                        onClick = {
                            calendarMonth.add(Calendar.MONTH, 1)
                            onMonthChange(calendarMonth.timeInMillis)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = PinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Weekday Header Labels (Sun, Mon, Tue, etc.)
                val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekdays.forEach { dayName ->
                        Text(
                            text = dayName,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF8B6B78)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF))
                Spacer(modifier = Modifier.height(8.dp))

                // Days Grid (chunks of 7)
                daysInMatrix.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { dayStat ->
                            if (dayStat != null) {
                                val hasTasks = dayStat.totalCount > 0
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = when {
                                        dayStat.isSelected -> PinkPrimary
                                        dayStat.isToday -> if (isDark) PinkPrimary.copy(alpha = 0.25f) else Color(0xFFFFF0F5)
                                        else -> Color.Transparent
                                    },
                                    border = when {
                                        dayStat.isToday && !dayStat.isSelected -> BorderStroke(1.dp, PinkPrimary)
                                        hasTasks && !dayStat.isSelected -> BorderStroke(
                                            1.dp,
                                            if (isDark) Color(0xFF383844) else Color(0xFFFCE7F3)
                                        )
                                        else -> null
                                    },
                                    onClick = { onSelectDate(dayStat.dateMillis) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .padding(horizontal = 2.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = dayStat.dateLabel,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (dayStat.isToday || dayStat.isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = when {
                                                dayStat.isSelected -> Color.White
                                                dayStat.isToday -> PinkPrimary
                                                else -> if (isDark) Color(0xFFE2E8F0) else DarkBlackText
                                            }
                                        )

                                        // Task count dots
                                        if (hasTasks) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                if (dayStat.completedCount > 0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(if (dayStat.isSelected) Color.White else PinkPrimary)
                                                    )
                                                }
                                                if (dayStat.completedCount > 0 && dayStat.pendingCount > 0) {
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                }
                                                if (dayStat.pendingCount > 0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(if (dayStat.isSelected) Color(0xFFFFD54F) else Color(0xFFF59E0B))
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                )
                            }
                        }

                        // Fill trailing spaces if last row has less than 7 days
                        if (week.size < 7) {
                            for (j in 0 until (7 - week.size)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectedDayTasksCard(
    tasks: List<TaskEntity>,
    selectedDateMillis: Long,
    onToggleTaskCompleted: (TaskEntity) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val dayStart = remember(selectedDateMillis) { DateTimeUtils.getStartOfDay(selectedDateMillis) }
    val dayEnd = remember(dayStart) { dayStart + 86_400_000L - 1L }

    val dayTasks = remember(tasks, dayStart, dayEnd) {
        tasks.filter { it.timestamp in dayStart..dayEnd }
    }

    val completedCount = remember(dayTasks) { dayTasks.count { it.isCompleted } }
    val pendingCount = remember(dayTasks) { dayTasks.count { !it.isCompleted } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color.White
            ),
            border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tasks for ${DateTimeUtils.getShortDate(selectedDateMillis)}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) Color.White else DarkBlackText
                        )
                        Text(
                            text = "$completedCount completed • $pendingCount pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B4E59)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) PinkPrimary.copy(alpha = 0.22f) else Color(0xFFFDE8EF)
                    ) {
                        Text(
                            text = "${dayTasks.size} Total",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PinkPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = if (isDark) MaterialTheme.colorScheme.outline else Color(0xFFFDE8EF))
                Spacer(modifier = Modifier.height(8.dp))

                if (dayTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks scheduled for this day.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF8B6B78)
                        )
                    }
                } else {
                    dayTasks.forEach { task ->
                        val taskCardBg = if (isDark) {
                            if (task.isCompleted) Color(0xFF16161A) else Color(0xFF222228)
                        } else {
                            if (task.isCompleted) Color(0xFFFFF9FB) else Color.White
                        }
                        val taskCardBorder = if (isDark) {
                            Color(0xFF383844)
                        } else {
                            if (task.isCompleted) Color(0xFFFDE8EF) else Color(0xFFF6E2EA)
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = taskCardBg,
                            border = BorderStroke(1.dp, taskCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onToggleTaskCompleted(task) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = if (task.isCompleted) "Mark pending" else "Mark completed",
                                        tint = if (task.isCompleted) PinkPrimary else (if (isDark) Color(0xFF64748B) else Color(0xFFB5A4AC)),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = if (task.isCompleted) {
                                            if (isDark) Color(0xFF94A3B8) else DarkBlackMutedText
                                        } else {
                                            if (isDark) Color.White else DarkBlackText
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = DateTimeUtils.formatTime(task.timestamp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B4E59)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val priorityColor = when (task.priority.lowercase()) {
                                            "high" -> PriorityHigh
                                            "medium" -> PriorityMedium
                                            else -> PriorityLow
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = priorityColor.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = task.priority.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = priorityColor,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Requested Footer: "Made with love for you"
 */
@Composable
fun MadeWithLoveFooter(modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Made with ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = if (isDark) Color(0xFFCBD5E1) else DarkBlackVariantText
        )
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Love",
            tint = PinkPrimary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = " love for you",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = if (isDark) Color(0xFFCBD5E1) else DarkBlackVariantText
        )
    }
}
