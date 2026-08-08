package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.MoodType

@Composable
fun MoodGrid(
    selectedMood: MoodType?,
    onMoodSelected: (MoodType) -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = MoodType.entries
    val chunkedMoods = moods.chunked(4)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        chunkedMoods.forEach { rowMoods ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowMoods.forEach { mood ->
                    Box(modifier = Modifier.weight(1f)) {
                        MoodCard(
                            mood = mood,
                            isSelected = selectedMood == mood,
                            onSelect = { onMoodSelected(mood) }
                        )
                    }
                }
                repeat(4 - rowMoods.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
