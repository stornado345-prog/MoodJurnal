package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormatIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatPretty = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val dateFormatShort = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val timeFormat12 = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun getCurrentDateString(): String {
        return dateFormatIso.format(Date())
    }

    fun getCurrentTimeString(): String {
        return timeFormat12.format(Date())
    }

    fun getTodayPrettyDate(): String {
        return dateFormatPretty.format(Date())
    }

    fun formatPrettyDate(timestamp: Long): String {
        return dateFormatPretty.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return dateFormatShort.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat12.format(Date(timestamp))
    }

    fun formatMonthYear(calendar: Calendar): String {
        return monthYearFormat.format(calendar.time)
    }

    fun parseIsoDate(dateString: String): Date? {
        return try {
            dateFormatIso.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun calculateStreaks(dateStrings: List<String>): Pair<Int, Int> {
        if (dateStrings.isEmpty()) return Pair(0, 0)

        val uniqueDates = dateStrings.mapNotNull { parseIsoDate(it) }
            .distinct()
            .sortedDescending()

        if (uniqueDates.isEmpty()) return Pair(0, 0)

        val calendar = Calendar.getInstance()
        val todayStr = getCurrentDateString()
        val today = parseIsoDate(todayStr) ?: Date()

        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time

        // Current streak
        var currentStreak = 0
        var checkDate = today

        val hasToday = uniqueDates.any { dateFormatIso.format(it) == todayStr }
        val hasYesterday = uniqueDates.any { dateFormatIso.format(it) == dateFormatIso.format(yesterday) }

        if (hasToday || hasYesterday) {
            var tempCal = Calendar.getInstance()
            tempCal.time = if (hasToday) today else yesterday

            while (true) {
                val dateStr = dateFormatIso.format(tempCal.time)
                if (uniqueDates.any { dateFormatIso.format(it) == dateStr }) {
                    currentStreak++
                    tempCal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
        }

        // Longest streak
        val sortedAscending = uniqueDates.sorted()
        var longestStreak = 0
        var tempStreak = 0
        var prevCal: Calendar? = null

        for (date in sortedAscending) {
            val currCal = Calendar.getInstance().apply { time = date }
            if (prevCal == null) {
                tempStreak = 1
            } else {
                val diffDays = ((currCal.timeInMillis - prevCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    tempStreak++
                } else if (diffDays > 1) {
                    tempStreak = 1
                }
            }
            if (tempStreak > longestStreak) {
                longestStreak = tempStreak
            }
            prevCal = currCal
        }

        return Pair(currentStreak, maxOf(currentStreak, longestStreak))
    }
}
