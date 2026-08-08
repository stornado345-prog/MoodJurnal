package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.entity.JournalEntry
import com.example.data.model.MoodType
import com.example.data.repository.JournalRepository
import com.example.utils.DateUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class NavigateToDetail(val entryId: Long) : UiEvent()
    object EntrySaved : UiEvent()
}

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    // UI Events
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // Raw entries
    val allEntries: StateFlow<List<JournalEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteEntries: StateFlow<List<JournalEntry>> = repository.favoriteEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val statistics: StateFlow<JournalRepository.StatisticsData> = repository.getStatistics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = JournalRepository.StatisticsData()
        )

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMoodFilter = MutableStateFlow<String?>(null) // null = all
    val selectedMoodFilter = _selectedMoodFilter.asStateFlow()

    private val _favoritesOnlyFilter = MutableStateFlow(false)
    val favoritesOnlyFilter = _favoritesOnlyFilter.asStateFlow()

    val filteredEntries: StateFlow<List<JournalEntry>> = combine(
        allEntries,
        _searchQuery,
        _selectedMoodFilter,
        _favoritesOnlyFilter
    ) { entries, query, moodFilter, favOnly ->
        entries.filter { entry ->
            val matchesQuery = query.isBlank() || entry.text.contains(query, ignoreCase = true) || entry.tags.contains(query, ignoreCase = true)
            val matchesMood = moodFilter == null || entry.moodId.equals(moodFilter, ignoreCase = true)
            val matchesFav = !favOnly || entry.isFavorite
            matchesQuery && matchesMood && matchesFav
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Entry Creation/Editing State
    private val _currentMood = MutableStateFlow(MoodType.HAPPY)
    val currentMood: StateFlow<MoodType> = _currentMood.asStateFlow()

    private val _journalText = MutableStateFlow("")
    val journalText: StateFlow<String> = _journalText.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _editingEntryId = MutableStateFlow<Long?>(null)
    val editingEntryId: StateFlow<Long?> = _editingEntryId.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMoodFilter(moodId: String?) {
        _selectedMoodFilter.value = moodId
    }

    fun setFavoritesOnlyFilter(favOnly: Boolean) {
        _favoritesOnlyFilter.value = favOnly
    }

    fun selectMoodForJournal(mood: MoodType) {
        _currentMood.value = mood
    }

    fun updateJournalText(text: String) {
        _journalText.value = text
    }

    fun setSelectedImageUri(uri: String?) {
        _selectedImageUri.value = uri
    }

    fun toggleCurrentFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun prepareNewJournal(initialMood: MoodType = MoodType.HAPPY) {
        _editingEntryId.value = null
        _currentMood.value = initialMood
        _journalText.value = ""
        _selectedImageUri.value = null
        _isFavorite.value = false
    }

    fun prepareEditJournal(entry: JournalEntry) {
        _editingEntryId.value = entry.id
        _currentMood.value = MoodType.fromId(entry.moodId)
        _journalText.value = entry.text
        _selectedImageUri.value = entry.imageUri
        _isFavorite.value = entry.isFavorite
    }

    fun saveJournalEntry() {
        val text = _journalText.value.trim()
        val mood = _currentMood.value
        val imageUri = _selectedImageUri.value
        val fav = _isFavorite.value
        val editingId = _editingEntryId.value

        viewModelScope.launch {
            if (editingId == null) {
                // New entry
                val newEntry = JournalEntry(
                    moodId = mood.id,
                    moodEmoji = mood.emoji,
                    moodName = mood.label,
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    dateString = DateUtils.getCurrentDateString(),
                    timeString = DateUtils.getCurrentTimeString(),
                    isFavorite = fav,
                    imageUri = imageUri
                )
                repository.insert(newEntry)
            } else {
                // Update existing entry
                val existing = repository.getEntryByIdDirect(editingId)
                if (existing != null) {
                    val updated = existing.copy(
                        moodId = mood.id,
                        moodEmoji = mood.emoji,
                        moodName = mood.label,
                        text = text,
                        isFavorite = fav,
                        imageUri = imageUri
                    )
                    repository.update(updated)
                }
            }
            _uiEvent.emit(UiEvent.EntrySaved)
            _uiEvent.emit(UiEvent.ShowToast("Journal saved successfully! ✨"))
            prepareNewJournal()
        }
    }

    fun toggleFavorite(entry: JournalEntry) {
        viewModelScope.launch {
            repository.toggleFavorite(entry.id, entry.isFavorite)
        }
    }

    fun deleteEntry(entryId: Long) {
        viewModelScope.launch {
            repository.deleteById(entryId)
            _uiEvent.emit(UiEvent.ShowToast("Journal entry deleted"))
        }
    }

    fun restoreBackup(entries: List<JournalEntry>) {
        viewModelScope.launch {
            repository.restoreBackup(entries)
            _uiEvent.emit(UiEvent.ShowToast("Imported ${entries.size} journals successfully!"))
        }
    }

    fun clearAllEntries() {
        viewModelScope.launch {
            repository.clearAllData()
            _uiEvent.emit(UiEvent.ShowToast("All journal data cleared"))
        }
    }
}
