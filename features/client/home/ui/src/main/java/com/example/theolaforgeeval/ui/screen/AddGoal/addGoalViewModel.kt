package com.example.theolaforgeeval.ui.screen.AddGoal

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

class AddGoalViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private var _uiState = MutableStateFlow(AddGoalUiState())
    val state: StateFlow<AddGoalUiState> = _uiState

    private var _uiEvents = Channel<AddGoalUiEvent>()
    val events: Flow<AddGoalUiEvent> = _uiEvents.receiveAsFlow()

    fun loadForEdit(id: Int) {
        viewModelScope.launch {
            categoryRepository.getCategoryById(id).collect { entity ->
                if (entity != null) {
                    _uiState.value = _uiState.value.copy(
                        editingCategory = entity,
                        name = entity.name,
                        color = Color(entity.color.toULong()),
                        icon = Translate.iconFromName(entity.iconName),
                        goalAmount = entity.goalAmount?.let { amount ->
                            if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()
                        } ?: "",
                        imagePath = entity.imagePath
                    )
                }
            }
        }
    }

    fun onAction(action: AddGoalUiAction) {
        when (action) {

            AddGoalUiAction.OnSave -> {
                saveGoal()
            }

            is AddGoalUiAction.OnNameChange -> {
                _uiState.value = _uiState.value.copy(name = action.value)
            }

            is AddGoalUiAction.OnColorSelect -> {
                _uiState.value = _uiState.value.copy(color = action.color)
            }

            is AddGoalUiAction.OnIconSelect -> {
                _uiState.value = _uiState.value.copy(icon = action.icon)
            }

            is AddGoalUiAction.OnGoalAmountChange -> {
                _uiState.value = _uiState.value.copy(goalAmount = action.value)
            }

            is AddGoalUiAction.OnImageSelected -> {
                _uiState.value = _uiState.value.copy(imagePath = action.path)
            }
        }
    }

    private fun saveGoal() {
        viewModelScope.launch {

            val current = _uiState.value

            val name = current.name.trim()

            if (name.isBlank()) {
                _uiEvents.send(AddGoalUiEvent.Error("Le nom est obligatoire"))
                return@launch
            }

            if (name.length < 2) {
                _uiEvents.send(AddGoalUiEvent.Error("Le nom doit contenir au moins 2 caractères"))
                return@launch
            }

            if (name.length > 50) {
                _uiEvents.send(AddGoalUiEvent.Error("Le nom ne peut pas dépasser 50 caractères"))
                return@launch
            }

            val goalAmount = current.goalAmount.trim().replace(',', '.').toDoubleOrNull()

            if (goalAmount == null || goalAmount <= 0.0) {
                _uiEvents.send(AddGoalUiEvent.Error("Le montant de l'objectif doit être supérieur à 0"))
                return@launch
            }

            val editing = current.editingCategory

            try {
                if (editing != null) {
                    categoryRepository.updateCategory(
                        editing.copy(
                            name = name,
                            color = current.color.toColorLong(),
                            iconName = Translate.imageVectorToName(current.icon),
                            goalAmount = goalAmount,
                            imagePath = current.imagePath
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
                            futurePrice = 0.0,
                            goalAmount = goalAmount,
                            imagePath = current.imagePath
                        )
                    )
                }

                _uiEvents.send(AddGoalUiEvent.Back)

            } catch (e: Exception) {
                _uiEvents.send(
                    AddGoalUiEvent.Error(
                        e.message ?: "Erreur inconnue"
                    )
                )
            }
        }
    }
}
