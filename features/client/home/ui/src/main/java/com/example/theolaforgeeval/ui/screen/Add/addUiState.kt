package com.example.theolaforgeeval.ui.screen.Add

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AddUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val name: String = "",
    val color: Color = Color(0xFFF5430B),
    val icon: ImageVector = Icons.Default.DirectionsCar
)

sealed class AddUiAction {
    data object ClickedBack : AddUiAction()
    data object OnSave : AddUiAction()

    data class OnNameChange(val value: String) : AddUiAction()
    data class OnColorSelect(val color: Color) : AddUiAction()
    data class OnIconSelect(val icon: ImageVector) : AddUiAction()
}

sealed class AddUiEvent {
    data object Back : AddUiEvent()
    data class Error(val message: String) : AddUiEvent()
}