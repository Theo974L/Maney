package com.example.theolaforgeeval.ui.screen.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.useCases.ExportDataUseCase
import com.example.theolaforgeeval.useCases.ImportDataUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase
) : ViewModel() {

    private val _uiEvents = Channel<SettingsUiEvent>()
    val events: Flow<SettingsUiEvent> = _uiEvents.receiveAsFlow()

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
