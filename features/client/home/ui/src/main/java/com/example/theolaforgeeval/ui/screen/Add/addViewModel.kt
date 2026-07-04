package com.example.theolaforgeeval.ui.screen.Add

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.ui.utils.Translate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random


class AddViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddUiState())
    val state: StateFlow<AddUiState> = _uiState

    private var _uiEvents = Channel<AddUiEvent>()
    val events: Flow<AddUiEvent> = _uiEvents.receiveAsFlow()

    fun onAction(action: AddUiAction) {
        when (action) {

            AddUiAction.ClickedBack -> {
                viewModelScope.launch {
                    _uiEvents.send(AddUiEvent.Back)
                }
            }

            AddUiAction.OnSave -> {
                saveCategory()
            }

            is AddUiAction.OnNameChange -> {
                _uiState.value = _uiState.value.copy(name = action.value)
            }

            is AddUiAction.OnColorSelect -> {
                _uiState.value = _uiState.value.copy(color = action.color)
            }

            is AddUiAction.OnIconSelect -> {
                _uiState.value = _uiState.value.copy(icon = action.icon)
            }
        }
    }

    private fun saveCategory() {
        viewModelScope.launch {

            val current = _uiState.value

            val name = current.name.trim()

            if (name.isBlank()) {
                _uiEvents.send(AddUiEvent.Error("Le nom est obligatoire"))
                return@launch
            }

            if (name.length < 2) {
                _uiEvents.send(AddUiEvent.Error("Le nom doit contenir au moins 2 caractères"))
                return@launch
            }

            if (name.length > 50) {
                _uiEvents.send(AddUiEvent.Error("Le nom ne peut pas dépasser 50 caractères"))
                return@launch
            }

            try {
                categoryRepository.insertCategories(
                    CategoryEntity(
                        id = 0,
                        name = name,
                        color = current.color.toColorLong(),
                        iconName = Translate.imageVectorToName(current.icon),
                        currentPrice = 0,
                        futurePrice = 0
                    )
                )

                _uiEvents.send(AddUiEvent.Back)

            } catch (e: Exception) {
                _uiEvents.send(
                    AddUiEvent.Error(
                        e.message ?: "Erreur inconnue"
                    )
                )
            }
        }
    }
}