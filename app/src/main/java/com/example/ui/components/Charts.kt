package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodPieChart(
    moodFrequencies: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    if (moodFrequencies.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No mood data to display",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val total = moodFrequencies.values.sum().toFloat()
    val progress = remember { Animatable(0f) }

    LaunchedEffect(moodFrequencies) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val moodEntries = moodFrequencies.entries.toList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mood_pie_chart"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                var startAngle = -90f
                val strokeWidth = 32.dp.toPx()

                for (entry in moodEntries) {
                    val mood = MoodType.entries.find { it.label.equals(entry.key, ignoreCase = true) } ?: MoodType.HAPPY
                    val sweepAngle = (entry.value / total) * 360f * progress.value

                    drawArc(
                        color = mood.primaryColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )

                    startAngle += sweepAngle
                }
            }

            Text(
                text = "${total.toInt()}\nEntries",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend FlowRow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (entry in moodEntries) {
                val mood = MoodType.entries.find { it.label.equals(entry.key, ignoreCase = true) } ?: MoodType.HAPPY
                val percentage = ((entry.value / total) * 100).toInt()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(mood.primaryColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${mood.emoji} ${mood.label} ($percentage%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityBarChart(
    title: String,
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val maxCount = maxOf(data.maxOfOrNull { it.second } ?: 1, 1)
    val progress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("activity_bar_chart_$title")
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val barColor = MaterialTheme.colorScheme.primary

            for (item in data) {
                val label = item.first
                val count = item.second
                val heightRatio = (count.toFloat() / maxCount.toFloat()) * progress.value

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (count > 0) count.toString() else "",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height((100 * heightRatio).dp.coerceAtLeast(4.dp))
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                if (count > 0) barColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
