package com.example.theolaforgeeval.ui.screen.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.theolaforgeeval.model.CategoryEntity
import java.util.Date

enum class ActionType {
    ADD,
    WITHDRAW,
    TRANSFER
}

data class ActionsUiState(
    val type: ActionType = ActionType.ADD,

    val amount: String = "",

    val date: Date = Date(),

    val sourceCategory: CategoryEntity? = null,

    val destinationCategory: CategoryEntity? = null,

    val categories: List<CategoryEntity> = emptyList()
)

sealed interface ActionsUiAction {

    data class OnTypeSelected(
        val type: ActionType
    ) : ActionsUiAction

    data class OnAmountChange(
        val amount: String
    ) : ActionsUiAction


    data class OnDateChange(
        val date: Date
    ) : ActionsUiAction


    data class OnSourceCategorySelected(
        val category: CategoryEntity
    ) : ActionsUiAction

    data class OnDestinationCategorySelected(
        val category: CategoryEntity
    ) : ActionsUiAction




    data object OnSave : ActionsUiAction
}
sealed class ActionsUiEvent {
    data object Back : ActionsUiEvent()
    data class Error(val message: String) : ActionsUiEvent()
}