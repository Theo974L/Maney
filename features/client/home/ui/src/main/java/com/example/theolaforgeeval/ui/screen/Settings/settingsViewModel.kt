package com.example.theolaforgeeval.ui.screen.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.core.preferences.AppPreferences
import com.example.theolaforgeeval.useCases.ExportDataUseCase
import com.example.theolaforgeeval.useCases.ImportDataUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiEvents = Channel<SettingsUiEvent>()
    val events: Flow<SettingsUiEvent> = _uiEvents.receiveAsFlow()

    val state: StateFlow<SettingsUiState> = combine(
        appPreferences.animationsEnabled,
        appPreferences.vibrationsEnabled,
        appPreferences.soundsEnabled
    ) { animations, vibrations, sounds ->
        SettingsUiState(
            animationsEnabled = animations,
            vibrationsEnabled = vibrations,
            soundsEnabled = sounds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is OnAnimationsToggle -> appPreferences.setAnimationsEnabled(action.enabled)
            is OnVibrationsToggle -> appPreferences.setVibrationsEnabled(action.enabled)
            is OnSoundsToggle -> appPreferences.setSoundsEnabled(action.enabled)
        }
    }

    fun requestExport() {
        viewModelScope.launch {
            try {
                val content = exportDataUseCase()
                _uiEvents.send(ExportReady(content))
            } catch (e: Exception) {
                _uiEvents.send(SettingsError(e.message ?: "Erreur lors de l'export"))
            }
        }
    }

    fun importContent(content: String) {
        viewModelScope.launch {
            try {
                importDataUseCase(content)
                _uiEvents.send(ImportSuccess)
            } catch (e: Exception) {
                _uiEvents.send(SettingsError("Fichier invalide : ${e.message ?: "erreur inconnue"}"))
            }
        }
    }
}
