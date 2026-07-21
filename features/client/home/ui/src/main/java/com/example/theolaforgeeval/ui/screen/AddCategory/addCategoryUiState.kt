package com.example.theolaforgeeval.ui.screen.AddCategory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.theolaforgeeval.model.CategoryEntity

data class AddCategoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val name: String = "",
    val color: Color = Color(0xFFF5430B),
    val icon: ImageVector = Icons.Default.DirectionsCar,

    val editingCategory: CategoryEntity? = null
) {
    val isEditMode: Boolean get() = editingCategory != null
}

sealed class AddCategoryUiAction {
    data object OnSave : AddCategoryUiAction()

    data class OnNameChange(val value: String) : AddCategoryUiAction()
    data class OnColorSelect(val color: Color) : AddCategoryUiAction()
    data class OnIconSelect(val icon: ImageVector) : AddCategoryUiAction()
}

sealed class AddCategoryUiEvent {
    data object Back : AddCategoryUiEvent()
    data class Error(val message: String) : AddCategoryUiEvent()
}
