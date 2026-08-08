package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MoodType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.JournalCard
import com.example.ui.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: JournalViewModel,
    onBackClick: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMoodFilter by viewModel.selectedMoodFilter.collectAsState()
    val favoritesOnlyFilter by viewModel.favoritesOnlyFilter.collectAsState()
    val entries by viewModel.filteredEntries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Search Reflections", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
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
                .testTag("search_screen")
        ) {
            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(text = "Search by keyword or tag...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("search_input_field")
            )

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = favoritesOnlyFilter,
                        onClick = { viewModel.setFavoritesOnlyFilter(!favoritesOnlyFilter) },
                        label = { Text("Favorites Only ❤️") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                items(MoodType.entries) { mood ->
                    val isSelected = selectedMoodFilter == mood.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setMoodFilter(if (isSelected) null else mood.id) },
                        label = { Text("${mood.emoji} ${mood.label}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (entries.isEmpty()) {
                EmptyStateView(
                    title = "No Matching Reflections",
                    description = "Try searching with different keywords or clearing active filters.",
                    buttonText = "Clear Search Filters",
                    onAddClick = {
                        viewModel.setSearchQuery("")
                        viewModel.setMoodFilter(null)
                        viewModel.setFavoritesOnlyFilter(false)
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = entries,
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
