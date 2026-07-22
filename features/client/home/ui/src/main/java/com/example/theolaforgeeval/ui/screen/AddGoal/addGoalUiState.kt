package com.example.theolaforgeeval.ui.screen.AddGoal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.theolaforgeeval.model.CategoryEntity

data class AddGoalUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val name: String = "",
    val color: Color = Color(0xFF22C55E),
    val icon: ImageVector = Icons.Default.Savings,

    val goalAmount: String = "",
    val imagePath: String? = null,

    val editingCategory: CategoryEntity? = null
) {
    val isEditMode: Boolean get() = editingCategory != null
}

sealed class AddGoalUiAction {
    data object OnSave : AddGoalUiAction()

    data class OnNameChange(val value: String) : AddGoalUiAction()
    data class OnColorSelect(val color: Color) : AddGoalUiAction()
    data class OnIconSelect(val icon: ImageVector) : AddGoalUiAction()
    data class OnGoalAmountChange(val value: String) : AddGoalUiAction()
    data class OnImageSelected(val path: String?) : AddGoalUiAction()
}

sealed class AddGoalUiEvent {
    data object Back : AddGoalUiEvent()
    data class Success(
        val playAnimation: Boolean,
        val playSound: Boolean,
        val playVibration: Boolean
    ) : AddGoalUiEvent()
    data class Error(val message: String) : AddGoalUiEvent()
}
