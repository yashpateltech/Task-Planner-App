package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.theme.DarkBlackMutedText
import com.example.ui.theme.DarkBlackText
import com.example.ui.theme.DarkBlackVariantText
import com.example.ui.theme.PinkGradients
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.utils.DateTimeUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskItemCard(
    task: TaskEntity,
    isFocused: Boolean,
    onToggleCompleted: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> PinkPrimary
            isDark -> if (task.isCompleted) Color(0xFF2E2E36) else Color(0xFF383844)
            task.isCompleted -> Color(0xFFF3DDE5).copy(alpha = 0.6f)
            else -> Color(0xFFF6D8E2)
        },
        label = "borderColor"
    )

    val containerColor = if (isDark) {
        if (task.isCompleted) Color(0xFF16161A).copy(alpha = 0.75f) else Color(0xFF1C1C22)
    } else {
        if (task.isCompleted) Color(0xFFFFFBFD) else Color.White
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isFocused) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Checkbox / Toggle Icon
                IconButton(
                    onClick = onToggleCompleted,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("task_toggle_${task.id}")
                ) {
                    if (task.isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(PinkGradients.Primary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Circle,
                            contentDescription = "Mark as completed",
                            tint = if (isDark) PinkPrimary else Color(0xFFF472B6),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Title and Description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                        ),
                        color = if (task.isCompleted) {
                            if (isDark) Color(0xFF94A3B8) else DarkBlackMutedText
                        } else {
                            if (isDark) Color(0xFFFFFFFF) else DarkBlackText
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (task.isCompleted) {
                                if (isDark) Color(0xFF64748B) else DarkBlackMutedText.copy(alpha = 0.8f)
                            } else {
                                if (isDark) Color(0xFFCBD5E1) else DarkBlackVariantText
                            },
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action buttons
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("task_edit_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit task",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF9E7E8B),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("task_delete_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete task",
                        tint = if (isDark) Color(0xFFFB7185) else Color(0xFFE11D48).copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp)
            ) {
                // Scheduled Date & Time Chip
                val dateChipBg = if (isDark) Color(0xFF2D1822) else Color(0xFFFFF0F5)
                val dateChipBorder = if (isDark) Color(0xFF4D2638) else Color(0xFFFCDDEC)
                val dateChipText = if (isDark) Color(0xFFFFB3C6) else Color(0xFF881337)

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = dateChipBg,
                    border = BorderStroke(0.5.dp, dateChipBorder),
                    modifier = Modifier.height(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PinkPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${DateTimeUtils.formatDate(task.timestamp)} • ${DateTimeUtils.formatTime(task.timestamp)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = dateChipText
                        )
                    }
                }

                // Alarm Offset Chip (if active)
                val alarmChipBg = if (isDark) {
                    if (task.isCompleted) Color(0xFF222228) else Color(0xFF281827)
                } else {
                    if (task.isCompleted) Color(0xFFF9F6F7) else Color(0xFFFCE7F3)
                }
                val alarmChipBorder = if (isDark) Color(0xFF3E283C) else Color(0xFFF9CFE2)
                val alarmChipText = if (isDark) {
                    if (task.isCompleted) Color(0xFF94A3B8) else Color(0xFFF0ABFC)
                } else {
                    if (task.isCompleted) Color(0xFFB5A4AC) else Color(0xFF701A75)
                }
                val alarmIconTint = if (task.isCompleted) {
                    if (isDark) Color(0xFF64748B) else Color(0xFFB5A4AC)
                } else {
                    if (isDark) Color(0xFFF472B6) else Color(0xFFBE185D)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = alarmChipBg,
                    border = BorderStroke(0.5.dp, alarmChipBorder),
                    modifier = Modifier.height(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = alarmIconTint,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        val offsetLabel = when (task.reminderOffsetMinutes) {
                            0 -> "At time"
                            else -> "${task.reminderOffsetMinutes}m before"
                        }
                        Text(
                            text = offsetLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = alarmChipText
                        )
                    }
                }

                // Priority Badge
                val priorityColor = when (task.priority.lowercase()) {
                    "high" -> PriorityHigh
                    "low" -> PriorityLow
                    else -> PriorityMedium
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = priorityColor.copy(alpha = if (isDark) 0.22f else 0.12f),
                    border = BorderStroke(1.dp, priorityColor.copy(alpha = if (isDark) 0.5f else 0.35f)),
                    modifier = Modifier.height(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = task.priority.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = priorityColor
                        )
                    }
                }
            }
        }
    }
}
