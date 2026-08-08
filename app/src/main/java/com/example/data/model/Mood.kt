package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class MoodType(
    val id: String,
    val emoji: String,
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val description: String
) {
    HAPPY("happy", "😊", "Happy", MoodHappyPrimary, MoodHappySecondary, "Feeling joyful and radiant"),
    EXCITED("excited", "😁", "Excited", MoodExcitedPrimary, MoodExcitedSecondary, "Energetic and enthusiastic"),
    CALM("calm", "😌", "Calm", MoodCalmPrimary, MoodCalmSecondary, "Peaceful and relaxed"),
    TIRED("tired", "😴", "Tired", MoodTiredPrimary, MoodTiredSecondary, "Drained and resting"),
    SAD("sad", "😢", "Sad", MoodSadPrimary, MoodSadSecondary, "Feeling down or blue"),
    ANGRY("angry", "😡", "Angry", MoodAngryPrimary, MoodAngrySecondary, "Frustrated or annoyed"),
    ANXIOUS("anxious", "😰", "Anxious", MoodAnxiousPrimary, MoodAnxiousSecondary, "Nervous or overwhelmed"),
    LOVED("loved", "😍", "Loved", MoodLovedPrimary, MoodLovedSecondary, "Cherished and affectionate");

    companion object {
        fun fromId(id: String): MoodType {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: HAPPY
        }
    }
}
