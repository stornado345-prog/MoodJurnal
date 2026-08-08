package com.example.data.repository

import com.example.data.database.dao.JournalDao
import com.example.data.database.entity.JournalEntry
import com.example.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class JournalRepository(private val journalDao: JournalDao) {

    private val dateFormatDay = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
    private val dateFormatIso = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    private val dateFormatMonth = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
    private val dateFormatYearMonth = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault())

    val allEntries: Flow<List<JournalEntry>> = journalDao.getAllEntries()
    val favoriteEntries: Flow<List<JournalEntry>> = journalDao.getFavoriteEntries()
    val totalCount: Flow<Int> = journalDao.getTotalCount()

    fun getEntryById(id: Long): Flow<JournalEntry?> = journalDao.getEntryById(id)

    suspend fun getEntryByIdDirect(id: Long): JournalEntry? = journalDao.getEntryByIdDirect(id)

    fun getEntriesByMood(moodId: String): Flow<List<JournalEntry>> = journalDao.getEntriesByMood(moodId)

    fun getEntriesByDate(dateString: String): Flow<List<JournalEntry>> = journalDao.getEntriesByDate(dateString)

    fun searchEntries(query: String): Flow<List<JournalEntry>> = journalDao.searchEntries(query)

    suspend fun insert(entry: JournalEntry): Long = journalDao.insertEntry(entry)

    suspend fun update(entry: JournalEntry) = journalDao.updateEntry(entry)

    suspend fun deleteById(id: Long) = journalDao.deleteEntryById(id)

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) {
        journalDao.setFavorite(id, !currentStatus)
    }

    suspend fun restoreBackup(entries: List<JournalEntry>) {
        journalDao.insertAll(entries)
    }

    suspend fun clearAllData() {
        journalDao.deleteAllEntries()
    }

    // Analytics Helper Data Class
    data class StatisticsData(
        val totalEntries: Int = 0,
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val mostCommonMoodEmoji: String = "😊",
        val mostCommonMoodName: String = "Happy",
        val moodFrequencies: Map<String, Int> = emptyMap(),
        val weeklyCounts: List<Pair<String, Int>> = emptyList(), // Day label -> Count
        val monthlyCounts: List<Pair<String, Int>> = emptyList(), // Month label -> Count
    )

    fun getStatistics(): Flow<StatisticsData> = allEntries.map { entries ->
        if (entries.isEmpty()) {
            return@map StatisticsData()
        }

        val total = entries.size
        val dateStrings = entries.map { it.dateString }
        val (currentStreak, longestStreak) = DateUtils.calculateStreaks(dateStrings)

        // Mood Frequencies
        val moodMap = mutableMapOf<String, Int>()
        entries.forEach {
            moodMap[it.moodName] = (moodMap[it.moodName] ?: 0) + 1
        }

        val mostCommon = moodMap.maxByOrNull { it.value }
        val mostCommonName = mostCommon?.key ?: "Happy"
        val mostCommonEmoji = entries.find { it.moodName == mostCommonName }?.moodEmoji ?: "😊"

        // Weekly Counts (Last 7 days)
        val weeklyList = mutableListOf<Pair<String, Int>>()
        val cal = Calendar.getInstance()

        for (i in 6 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            
            val dateStr = dateFormatIso.format(cal.time)
            val dayLabel = dateFormatDay.format(cal.time)
            val count = entries.count { it.dateString == dateStr }
            weeklyList.add(Pair(dayLabel, count))
        }

        // Monthly Counts (Last 6 months)
        val monthlyList = mutableListOf<Pair<String, Int>>()

        for (i in 5 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.MONTH, -i)
            
            val ymStr = dateFormatYearMonth.format(cal.time)
            val monthLabel = dateFormatMonth.format(cal.time)
            val count = entries.count { it.dateString.startsWith(ymStr) }
            monthlyList.add(Pair(monthLabel, count))
        }

        StatisticsData(
            totalEntries = total,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            mostCommonMoodEmoji = mostCommonEmoji,
            mostCommonMoodName = mostCommonName,
            moodFrequencies = moodMap,
            weeklyCounts = weeklyList,
            monthlyCounts = monthlyList
        )
    }
}
