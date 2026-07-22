package com.example.theolaforgeeval.ui.screen.actions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.core.preferences.AppPreferences
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import com.example.theolaforgeeval.model.TransactionActionEntity
import com.example.theolaforgeeval.repository.TransactionRepository
import com.example.theolaforgeeval.useCases.GetCategoryTotalUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

private fun String.toAmountOrNull(): Double? = trim().replace(',', '.').toDoubleOrNull()

private fun Double.toEditableString(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

class ActionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val getCategoryTotalUseCase: GetCategoryTotalUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private var _uiState = MutableStateFlow(ActionsUiState())
    val state: StateFlow<ActionsUiState> = _uiState

    private var _uiEvents = Channel<ActionsUiEvent>()
    val events: Flow<ActionsUiEvent> = _uiEvents.receiveAsFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoryTotalUseCase().collect { list ->
                _uiState.value = _uiState.value.copy(
                    categories = list
                )
            }
        }
    }

    fun loadForEdit(transactionId: Int) {
        viewModelScope.launch {
            val categories = getCategoryTotalUseCase().first()
            val entity = transactionRepository.getTransactionById(transactionId).first() ?: return@launch

            val sourceCategory = categories.firstOrNull { it.id == entity.categorySourceId }
            val destCategory = entity.categoryDestId?.let { destId -> categories.firstOrNull { it.id == destId } }

            val type = when {
                entity.categoryDestId != null -> ActionType.TRANSFER
                entity.amountValue < 0 -> ActionType.WITHDRAW
                else -> ActionType.ADD
            }

            _uiState.value = _uiState.value.copy(
                editingTransactionId = entity.id,
                type = type,
                amount = abs(entity.amountValue).toEditableString(),
                date = Date(entity.dateInfo),
                sourceCategory = sourceCategory,
                destinationCategory = destCategory
            )
        }
    }

    fun onAction(action: ActionsUiAction) {
        when (action) {

            is ActionsUiAction.OnTypeSelected -> {
                _uiState.value = _uiState.value.copy(
                    type = action.type
                )
            }

            is ActionsUiAction.OnAmountChange -> {
                _uiState.value = _uiState.value.copy(
                    amount = action.amount
                )
            }

            is ActionsUiAction.OnSourceCategorySelected -> {
                _uiState.value = _uiState.value.copy(
                    sourceCategory = action.category
                )
            }

            is ActionsUiAction.OnDestinationCategorySelected -> {
                _uiState.value = _uiState.value.copy(
                    destinationCategory = action.category
                )
            }

            is ActionsUiAction.OnDateChange -> {
                _uiState.value = _uiState.value.copy(
                    date = action.date
                )
            }

            ActionsUiAction.OnSave -> {
                saveAction()
            }
        }
    }

    private fun saveAction() {
        viewModelScope.launch {

            val current = _uiState.value
            val id = current.editingTransactionId ?: 0

            if (current.amount.isBlank()) {
                _uiEvents.send(ActionsUiEvent.Error("Montant invalide"))
                return@launch
            }

            val amount = current.amount.toAmountOrNull()

            if (amount == null || amount <= 0.0) {
                _uiEvents.send(ActionsUiEvent.Error("Montant invalide"))
                return@launch
            }

            val date = current.date
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)
            val formattedDate = "${dateFormatter.format(date)} • ${timeFormatter.format(date)}"

            when (current.type) {

                ActionType.ADD -> {

                    val source = current.sourceCategory
                    if (source == null) {
                        _uiEvents.send(ActionsUiEvent.Error("Choisis une catégorie"))
                        return@launch
                    }

                    val entity = TransactionActionEntity(
                        id = id,
                        icon = "Add",
                        title = "Ajout",
                        description = "Ajout vers : ${source.name}",
                        amount = "+ ${amount.formatEuro()}",
                        amountColor = Color.Green.toColorLong(),
                        date = formattedDate,
                        amountValue = amount,
                        dateInfo = date.time,
                        categorySourceId = source.id
                    )

                    if (current.editingTransactionId != null) {
                        transactionRepository.updateTransaction(entity)
                    } else {
                        transactionRepository.insertTransaction(entity)
                    }
                }

                ActionType.WITHDRAW -> {

                    val source = current.sourceCategory
                    if (source == null) {
                        _uiEvents.send(ActionsUiEvent.Error("Choisis une catégorie"))
                        return@launch
                    }

                    if (source.currentPrice < amount) {
                        _uiEvents.send(ActionsUiEvent.Error("Solde insuffisant sur cette catégorie"))
                        return@launch
                    }

                    val entity = TransactionActionEntity(
                        id = id,
                        icon = "Remove",
                        title = "Retrait",
                        description = "Retrait vers : ${source.name}",
                        amount = "- ${amount.formatEuro()}",
                        amountColor = Color.Red.toColorLong(),
                        date = formattedDate,
                        amountValue = -amount,
                        dateInfo = date.time,
                        categorySourceId = source.id
                    )

                    if (current.editingTransactionId != null) {
                        transactionRepository.updateTransaction(entity)
                    } else {
                        transactionRepository.insertTransaction(entity)
                    }
                }

                ActionType.TRANSFER -> {

                    val source = current.sourceCategory
                    if (source == null) {
                        _uiEvents.send(ActionsUiEvent.Error("Choisis une catégorie de départ"))
                        return@launch
                    }

                    val dest = current.destinationCategory
                    if (dest == null) {
                        _uiEvents.send(ActionsUiEvent.Error("Choisis une catégorie de destination"))
                        return@launch
                    }

                    if (source.id == dest.id) {
                        _uiEvents.send(ActionsUiEvent.Error("Les deux catégories doivent être différentes"))
                        return@launch
                    }

                    if (source.currentPrice < amount) {
                        _uiEvents.send(ActionsUiEvent.Error("Solde insuffisant sur la catégorie de départ"))
                        return@launch
                    }

                    val entity = TransactionActionEntity(
                        id = id,
                        icon = "SwapHoriz",
                        title = "Transfert",
                        description = "${source.name} → ${dest.name}",
                        amount = "↪ ${amount.formatEuro()}",
                        amountColor = Color.Yellow.toColorLong(),
                        date = formattedDate,
                        amountValue = amount,
                        dateInfo = date.time,
                        categorySourceId = source.id,
                        categoryDestId = dest.id
                    )

                    if (current.editingTransactionId != null) {
                        transactionRepository.updateTransaction(entity)
                    } else {
                        transactionRepository.insertTransaction(entity)
                    }
                }
            }
            _uiEvents.send(
                ActionsUiEvent.Success(
                    playAnimation = appPreferences.animationsEnabled.value,
                    playSound = appPreferences.soundsEnabled.value,
                    playVibration = appPreferences.vibrationsEnabled.value
                )
            )
        }
    }
}
