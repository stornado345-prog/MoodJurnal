package com.example.utils

import com.example.data.database.entity.JournalEntry
import org.json.JSONArray
import org.json.JSONObject

object BackupManager {

    fun exportToJson(entries: List<JournalEntry>): String {
        val jsonArray = JSONArray()
        for (entry in entries) {
            val obj = JSONObject().apply {
                put("id", entry.id)
                put("moodId", entry.moodId)
                put("moodEmoji", entry.moodEmoji)
                put("moodName", entry.moodName)
                put("text", entry.text)
                put("timestamp", entry.timestamp)
                put("dateString", entry.dateString)
                put("timeString", entry.timeString)
                put("isFavorite", entry.isFavorite)
                put("imageUri", entry.imageUri ?: "")
                put("tags", entry.tags)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importFromJson(jsonString: String): List<JournalEntry> {
        val list = mutableListOf<JournalEntry>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val entry = JournalEntry(
                    id = obj.optLong("id", 0),
                    moodId = obj.optString("moodId", "happy"),
                    moodEmoji = obj.optString("moodEmoji", "😊"),
                    moodName = obj.optString("moodName", "Happy"),
                    text = obj.optString("text", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    dateString = obj.optString("dateString", DateUtils.getCurrentDateString()),
                    timeString = obj.optString("timeString", DateUtils.getCurrentTimeString()),
                    isFavorite = obj.optBoolean("isFavorite", false),
                    imageUri = obj.optString("imageUri", "").ifEmpty { null },
                    tags = obj.optString("tags", "")
                )
                list.add(entry)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
