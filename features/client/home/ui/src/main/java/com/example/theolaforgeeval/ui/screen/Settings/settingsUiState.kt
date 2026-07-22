package com.example.theolaforgeeval.ui.screen.Settings

data class SettingsUiState(
    val animationsEnabled: Boolean = true,
    val vibrationsEnabled: Boolean = true,
    val soundsEnabled: Boolean = true
)

sealed interface SettingsUiAction
data class OnAnimationsToggle(val enabled: Boolean) : SettingsUiAction
data class OnVibrationsToggle(val enabled: Boolean) : SettingsUiAction
data class OnSoundsToggle(val enabled: Boolean) : SettingsUiAction

sealed interface SettingsUiEvent
data class ExportReady(val content: String) : SettingsUiEvent
data class SettingsError(val message: String) : SettingsUiEvent
data object ImportSuccess : SettingsUiEvent
