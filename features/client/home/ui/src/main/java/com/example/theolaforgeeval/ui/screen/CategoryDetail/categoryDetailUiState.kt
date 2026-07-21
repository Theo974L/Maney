package com.example.theolaforgeeval.ui.screen.CategoryDetail

import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.TransactionActionEntity

data class CategoryDetailUiState(
    val category: Categorie? = null,
    val transactions: List<TransactionActionEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showDeleteCategoryConfirm: Boolean = false,
    val transactionPendingDelete: TransactionActionEntity? = null
)

sealed interface CategoryDetailUiAction
data object OnEditClick : CategoryDetailUiAction
data object OnDeleteCategoryClick : CategoryDetailUiAction
data object OnConfirmDeleteCategory : CategoryDetailUiAction
data object OnDismissDeleteCategory : CategoryDetailUiAction
data class OnDeleteTransactionClick(val transaction: TransactionActionEntity) : CategoryDetailUiAction
data object OnConfirmDeleteTransaction : CategoryDetailUiAction
data object OnDismissDeleteTransaction : CategoryDetailUiAction
data class OnTransactionClick(val transaction: TransactionActionEntity) : CategoryDetailUiAction

sealed interface CategoryDetailUiEvent
data object NavigateBack : CategoryDetailUiEvent
data class NavigateToEdit(val categoryId: Int, val isGoal: Boolean) : CategoryDetailUiEvent
data class NavigateToEditTransaction(val transactionId: Int) : CategoryDetailUiEvent
