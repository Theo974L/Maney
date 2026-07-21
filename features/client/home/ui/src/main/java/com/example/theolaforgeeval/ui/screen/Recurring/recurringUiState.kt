package com.example.theolaforgeeval.ui.screen.Recurring

import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.RecurrenceFrequency
import com.example.theolaforgeeval.model.RecurringTransactionEntity
import com.example.theolaforgeeval.ui.screen.actions.ActionType

data class RecurringRuleUi(
    val entity: RecurringTransactionEntity,
    val categoryName: String,
    val nextOccurrenceLabel: String
)

data class RecurringUiState(
    val rules: List<RecurringRuleUi> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),

    val title: String = "",
    val amount: String = "",
    val type: ActionType = ActionType.WITHDRAW,
    val category: CategoryEntity? = null,
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,

    val pendingDelete: RecurringTransactionEntity? = null
)

sealed interface RecurringUiAction
data class OnTitleChange(val value: String) : RecurringUiAction
data class OnAmountChange(val value: String) : RecurringUiAction
data class OnTypeSelected(val type: ActionType) : RecurringUiAction
data class OnCategorySelected(val category: CategoryEntity) : RecurringUiAction
data class OnFrequencySelected(val frequency: RecurrenceFrequency) : RecurringUiAction
data object OnAddRule : RecurringUiAction
data class OnToggleActive(val rule: RecurringTransactionEntity, val active: Boolean) : RecurringUiAction
data class OnDeleteRuleClick(val rule: RecurringTransactionEntity) : RecurringUiAction
data object OnConfirmDeleteRule : RecurringUiAction
data object OnDismissDeleteRule : RecurringUiAction

sealed interface RecurringUiEvent
data class RecurringError(val message: String) : RecurringUiEvent
