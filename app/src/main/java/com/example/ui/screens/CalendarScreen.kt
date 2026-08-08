package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.JournalCard
import com.example.ui.components.MoodCalendarView
import com.example.ui.viewmodel.JournalViewModel
import com.example.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: JournalViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    val allEntries by viewModel.allEntries.collectAsState()
    var selectedDate by remember { mutableStateOf<String?>(DateUtils.getCurrentDateString()) }

    val dateEntries = remember(allEntries, selectedDate) {
        if (selectedDate == null) emptyList()
        else allEntries.filter { it.dateString == selectedDate }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mood Calendar 📅", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("calendar_screen")
        ) {
            // Calendar Widget
            MoodCalendarView(
                entries = allEntries,
                onDateSelected = { date -> selectedDate = date }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Entries for selected date
            Text(
                text = if (selectedDate != null) "Reflections for $selectedDate" else "Select a date to view entries",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (dateEntries.isEmpty()) {
                Text(
                    text = "No journal reflections recorded for this date.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = dateEntries,
                        key = { it.id }
                    ) { entry ->
                        JournalCard(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(entry) }
                        )
                    }
                }
            }
        }
    }
}
