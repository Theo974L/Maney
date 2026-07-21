package com.example.theolaforgeeval.ui.screen.AddCategory

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.ui.utils.Translate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddCategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddCategoryUiState())
    val state: StateFlow<AddCategoryUiState> = _uiState

    private var _uiEvents = Channel<AddCategoryUiEvent>()
    val events: Flow<AddCategoryUiEvent> = _uiEvents.receiveAsFlow()

    fun loadForEdit(id: Int) {
        viewModelScope.launch {
            categoryRepository.getCategoryById(id).collect { entity ->
                if (entity != null) {
                    _uiState.value = _uiState.value.copy(
                        editingCategory = entity,
                        name = entity.name,
                        color = Color(entity.color.toULong()),
                        icon = Translate.iconFromName(entity.iconName)
                    )
                }
            }
        }
    }

    fun onAction(action: AddCategoryUiAction) {
        when (action) {

            AddCategoryUiAction.OnSave -> {
                saveCategory()
            }

            is AddCategoryUiAction.OnNameChange -> {
                _uiState.value = _uiState.value.copy(name = action.value)
            }

            is AddCategoryUiAction.OnColorSelect -> {
                _uiState.value = _uiState.value.copy(color = action.color)
            }

            is AddCategoryUiAction.OnIconSelect -> {
                _uiState.value = _uiState.value.copy(icon = action.icon)
            }
        }
    }

    private fun saveCategory() {
        viewModelScope.launch {

            val current = _uiState.value

            val name = current.name.trim()

            if (name.isBlank()) {
                _uiEvents.send(AddCategoryUiEvent.Error("Le nom est obligatoire"))
                return@launch
            }

            if (name.length < 2) {
                _uiEvents.send(AddCategoryUiEvent.Error("Le nom doit contenir au moins 2 caractères"))
                return@launch
            }

            if (name.length > 50) {
                _uiEvents.send(AddCategoryUiEvent.Error("Le nom ne peut pas dépasser 50 caractères"))
                return@launch
            }

            val editing = current.editingCategory

            try {
                if (editing != null) {
                    categoryRepository.updateCategory(
                        editing.copy(
                            name = name,
                            color = current.color.toColorLong(),
                            iconName = Translate.imageVectorToName(current.icon)
                        )
                    )
                } else {
                    categoryRepository.insertCategories(
                        CategoryEntity(
                            id = 0,
                            name = name,
                            color = current.color.toColorLong(),
                            iconName = Translate.imageVectorToName(current.icon),
                            currentPrice = 0.0,
                            futurePrice = 0.0
                        )
                    )
                }

                _uiEvents.send(AddCategoryUiEvent.Back)

            } catch (e: Exception) {
                _uiEvents.send(
                    AddCategoryUiEvent.Error(
                        e.message ?: "Erreur inconnue"
                    )
                )
            }
        }
    }
}
