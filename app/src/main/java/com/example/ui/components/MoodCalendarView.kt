package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.entity.JournalEntry
import com.example.data.model.MoodType
import com.example.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CalendarDay(
    val dateString: String, // yyyy-MM-dd
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val mood: MoodType? = null,
    val entryCount: Int = 0
)

@Composable
fun MoodCalendarView(
    entries: List<JournalEntry>,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    val monthYearText = remember(calendar.timeInMillis) {
        DateUtils.formatMonthYear(calendar)
    }

    val daysInMonth = remember(calendar.timeInMillis, entries) {
        val days = mutableListOf<CalendarDay>()
        val calCopy = calendar.clone() as Calendar
        calCopy.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calCopy.get(Calendar.DAY_OF_WEEK) - 1 // 0-based index
        val maxDays = calCopy.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Previous month padding
        val prevCal = calCopy.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val maxPrevDays = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in (maxPrevDays - firstDayOfWeek + 1)..maxPrevDays) {
            prevCal.set(Calendar.DAY_OF_MONTH, i)
            days.add(
                CalendarDay(
                    dateString = sdf.format(prevCal.time),
                    dayNumber = i,
                    isCurrentMonth = false
                )
            )
        }

        // Current month
        val entryMap = entries.groupBy { it.dateString }

        for (day in 1..maxDays) {
            calCopy.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = sdf.format(calCopy.time)
            val dayEntries = entryMap[dateStr] ?: emptyList()
            val primaryMood = dayEntries.firstOrNull()?.let { MoodType.fromId(it.moodId) }

            days.add(
                CalendarDay(
                    dateString = dateStr,
                    dayNumber = day,
                    isCurrentMonth = true,
                    mood = primaryMood,
                    entryCount = dayEntries.size
                )
            )
        }

        // Next month padding
        val remaining = (7 - (days.size % 7)) % 7
        val nextCal = calCopy.clone() as Calendar
        nextCal.add(Calendar.MONTH, 1)

        for (i in 1..remaining) {
            nextCal.set(Calendar.DAY_OF_MONTH, i)
            days.add(
                CalendarDay(
                    dateString = sdf.format(nextCal.time),
                    dayNumber = i,
                    isCurrentMonth = false
                )
            )
        }

        days
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mood_calendar_view")
    ) {
        // Calendar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newCal = calendar.clone() as Calendar
                    newCal.add(Calendar.MONTH, -1)
                    calendar = newCal
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }

            Text(
                text = monthYearText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                onClick = {
                    val newCal = calendar.clone() as Calendar
                    newCal.add(Calendar.MONTH, 1)
                    calendar = newCal
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Days of Week Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            for (day in weekDays) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            daysInMonth.chunked(7).forEach { weekDays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weekDays.forEach { day ->
                        val isToday = day.dateString == DateUtils.getCurrentDateString()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        day.mood != null -> day.mood.primaryColor.copy(alpha = 0.25f)
                                        isToday -> MaterialTheme.colorScheme.primaryContainer
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (isToday) 2.dp else 0.dp,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = day.isCurrentMonth) {
                                    onDateSelected(day.dateString)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.dayNumber.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (day.mood != null || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                                        day.mood != null -> day.mood.primaryColor
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )

                                if (day.mood != null) {
                                    Text(
                                        text = day.mood.emoji,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                    repeat(7 - weekDays.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
