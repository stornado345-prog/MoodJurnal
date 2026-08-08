package com.example.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moodId: String,
    val moodEmoji: String,
    val moodName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // format "yyyy-MM-dd"
    val timeString: String, // format "HH:mm"
    val isFavorite: Boolean = false,
    val imageUri: String? = null,
    val tags: String = ""
)
