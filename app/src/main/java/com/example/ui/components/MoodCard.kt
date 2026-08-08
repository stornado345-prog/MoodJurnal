package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodType

@Composable
fun MoodCard(
    mood: MoodType,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isSelected -> 1.05f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "MoodCardScale"
    )

    val moodGradient = remember(mood) {
        Brush.linearGradient(
            colors = listOf(
                mood.primaryColor,
                mood.secondaryColor
            )
        )
    }

    val surfaceColors = listOf(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceVariant
    )
    val unselectedGradient = remember(surfaceColors) {
        Brush.linearGradient(colors = surfaceColors)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 10.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = mood.primaryColor
            )
            .clip(RoundedCornerShape(24.dp))
            .background(brush = if (isSelected) moodGradient else unselectedGradient)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                isPressed = !isPressed
                onSelect()
            }
            .padding(16.dp)
            .testTag("mood_card_${mood.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else mood.secondaryColor.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mood.emoji,
                    fontSize = 32.sp
                )
            }

            Text(
                text = mood.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
