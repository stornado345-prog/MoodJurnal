package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utils.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val isOnboardingCompleted: StateFlow<Boolean> = dataStoreManager.isOnboardingCompleted
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val themeMode: StateFlow<String> = dataStoreManager.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "SYSTEM"
        )

    val isDynamicColor: StateFlow<Boolean> = dataStoreManager.isDynamicColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val notificationsEnabled: StateFlow<Boolean> = dataStoreManager.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setOnboardingCompleted(completed)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            dataStoreManager.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDynamicColor(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setNotificationsEnabled(enabled)
        }
    }
}
