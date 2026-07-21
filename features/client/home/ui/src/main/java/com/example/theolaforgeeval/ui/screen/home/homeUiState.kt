package com.example.theolaforgeeval.ui.screen.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.TransactionAction
import com.example.theolaforgeeval.useCases.CategoryBreakdown

data class HomeUiState(
    val total: Double = 0.0,
    val futureTotal: Double = 0.0,
    val categories: List<Categorie> = emptyList(),
    val goals: List<Categorie> = emptyList(),
    val actions: List<TransactionAction> = emptyList(),
    val oldActions: List<TransactionAction> = emptyList(),
    val monthlyBreakdown: List<CategoryBreakdown> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)



sealed interface HomeUiAction
data class OnClickDelete(val categorie: Categorie) : HomeUiAction
data class OnGoalCelebrated(val categorie: Categorie) : HomeUiAction


sealed interface HomeUiEvent
    data class Error(val message: String) :  HomeUiEvent

