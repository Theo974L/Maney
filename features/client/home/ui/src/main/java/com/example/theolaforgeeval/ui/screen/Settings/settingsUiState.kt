package com.example.theolaforgeeval.ui.screen.Settings

sealed interface SettingsUiEvent
data class ExportReady(val content: String) : SettingsUiEvent
data class SettingsError(val message: String) : SettingsUiEvent
data object ImportSuccess : SettingsUiEvent
