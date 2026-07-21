package com.example.theolaforgeeval.ui.screen.Recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.RecurringTransactionEntity
import com.example.theolaforgeeval.repository.RecurringTransactionRepository
import com.example.theolaforgeeval.ui.screen.actions.ActionType
import com.example.theolaforgeeval.useCases.GetCategoryTotalUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecurringViewModel(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val getCategoryTotalUseCase: GetCategoryTotalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecurringUiState())
    val state: StateFlow<RecurringUiState> = _uiState

    private val _uiEvents = Channel<RecurringUiEvent>()
    val events: Flow<RecurringUiEvent> = _uiEvents.receiveAsFlow()

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    init {
        viewModelScope.launch {
            combine(
                getCategoryTotalUseCase(),
                recurringTransactionRepository.getAll()
            ) { categories, rules -> categories to rules }
                .collect { (categories, rules) ->
                    _uiState.update {
                        it.copy(
                            categories = categories,
                            rules = rules.map { rule -> toUi(rule, categories) }
                        )
                    }
                }
        }
    }

    private fun toUi(rule: RecurringTransactionEntity, categories: List<CategoryEntity>): RecurringRuleUi {
        val categoryName = categories.firstOrNull { it.id == rule.categorySourceId }?.name ?: "?"
        return RecurringRuleUi(
            entity = rule,
            categoryName = categoryName,
            nextOccurrenceLabel = dateFormatter.format(Date(rule.nextOccurrence))
        )
    }

    fun onAction(action: RecurringUiAction) {
        when (action) {
            is OnTitleChange -> _uiState.update { it.copy(title = action.value) }
            is OnAmountChange -> _uiState.update { it.copy(amount = action.value) }
            is OnTypeSelected -> _uiState.update { it.copy(type = action.type) }
            is OnCategorySelected -> _uiState.update { it.copy(category = action.category) }
            is OnFrequencySelected -> _uiState.update { it.copy(frequency = action.frequency) }
            OnAddRule -> addRule()
            is OnToggleActive -> {
                viewModelScope.launch {
                    recurringTransactionRepository.update(action.rule.copy(active = action.active))
                }
            }
            is OnDeleteRuleClick -> _uiState.update { it.copy(pendingDelete = action.rule) }
            OnDismissDeleteRule -> _uiState.update { it.copy(pendingDelete = null) }
            OnConfirmDeleteRule -> {
                val rule = _uiState.value.pendingDelete
                _uiState.update { it.copy(pendingDelete = null) }
                if (rule != null) {
                    viewModelScope.launch {
                        recurringTransactionRepository.delete(rule)
                    }
                }
            }
        }
    }

    private fun addRule() {
        viewModelScope.launch {
            val current = _uiState.value

            val title = current.title.trim()
            if (title.isBlank()) {
                _uiEvents.send(RecurringError("Le nom est obligatoire"))
                return@launch
            }

            val amountRaw = current.amount.trim().replace(',', '.').toDoubleOrNull()
            if (amountRaw == null || amountRaw <= 0.0) {
                _uiEvents.send(RecurringError("Montant invalide"))
                return@launch
            }

            val category = current.category
            if (category == null) {
                _uiEvents.send(RecurringError("Choisis une catégorie"))
                return@launch
            }

            val signedAmount = if (current.type == ActionType.WITHDRAW) -amountRaw else amountRaw

            recurringTransactionRepository.insert(
                RecurringTransactionEntity(
                    id = 0,
                    title = title,
                    amountValue = signedAmount,
                    icon = category.iconName,
                    frequency = current.frequency,
                    nextOccurrence = System.currentTimeMillis(),
                    categorySourceId = category.id
                )
            )

            _uiState.update { it.copy(title = "", amount = "", category = null) }
        }
    }
}
