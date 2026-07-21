package com.example.theolaforgeeval.ui.screen.CategoryDetail

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import com.example.theolaforgeeval.ui.utils.Translate
import com.example.theolaforgeeval.useCases.DeleteCategoryUseCase
import com.example.theolaforgeeval.useCases.GetCategoryTotalUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    private val getCategoryTotalUseCase: GetCategoryTotalUseCase,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val state: StateFlow<CategoryDetailUiState> = _uiState

    private val _uiEvents = Channel<CategoryDetailUiEvent>()
    val events: Flow<CategoryDetailUiEvent> = _uiEvents.receiveAsFlow()

    fun onStart(categoryId: Int) {
        viewModelScope.launch {
            getCategoryTotalUseCase().collect { categories ->
                val match = categories.firstOrNull { it.id == categoryId }

                if (match == null) {
                    _uiEvents.send(NavigateBack)
                } else {
                    _uiState.update {
                        it.copy(
                            category = Categorie(
                                entity = match,
                                nom = match.name,
                                color = Color(match.color.toULong()),
                                icon = Translate.iconFromName(match.iconName),
                                currentPrice = match.currentPrice,
                                futurePrice = match.futurePrice,
                                goalAmount = match.goalAmount,
                                imagePath = match.imagePath
                            ),
                            isLoading = false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            transactionRepository.getTransactionsForCategory(categoryId).collect { transactions ->
                _uiState.update { it.copy(transactions = transactions) }
            }
        }
    }

    fun onAction(action: CategoryDetailUiAction) {
        when (action) {
            OnEditClick -> {
                val category = _uiState.value.category ?: return
                viewModelScope.launch {
                    _uiEvents.send(NavigateToEdit(category.entity.id, category.isGoal))
                }
            }

            OnDeleteCategoryClick -> {
                _uiState.update { it.copy(showDeleteCategoryConfirm = true) }
            }

            OnDismissDeleteCategory -> {
                _uiState.update { it.copy(showDeleteCategoryConfirm = false) }
            }

            OnConfirmDeleteCategory -> {
                val category = _uiState.value.category
                _uiState.update { it.copy(showDeleteCategoryConfirm = false) }
                if (category != null) {
                    viewModelScope.launch {
                        deleteCategoryUseCase(category.entity)
                        _uiEvents.send(NavigateBack)
                    }
                }
            }

            is OnDeleteTransactionClick -> {
                _uiState.update { it.copy(transactionPendingDelete = action.transaction) }
            }

            OnDismissDeleteTransaction -> {
                _uiState.update { it.copy(transactionPendingDelete = null) }
            }

            OnConfirmDeleteTransaction -> {
                val transaction = _uiState.value.transactionPendingDelete
                _uiState.update { it.copy(transactionPendingDelete = null) }
                if (transaction != null) {
                    viewModelScope.launch {
                        transactionRepository.deleteTransaction(transaction)
                    }
                }
            }

            is OnTransactionClick -> {
                viewModelScope.launch {
                    _uiEvents.send(NavigateToEditTransaction(action.transaction.id))
                }
            }
        }
    }
}
