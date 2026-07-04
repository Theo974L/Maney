package com.example.theolaforgeeval.ui.screen.actions

import android.graphics.drawable.Icon
import android.icu.text.DateFormat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.TransactionAction
import com.example.theolaforgeeval.model.TransactionActionEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import com.example.theolaforgeeval.ui.screen.Add.AddUiEvent
import com.example.theolaforgeeval.ui.utils.Translate
import com.example.theolaforgeeval.useCases.GetCategoryTotalUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


class ActionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val getCategoryTotalUseCase: GetCategoryTotalUseCase
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
            val source = _uiState.value.sourceCategory
            val dest = _uiState.value.destinationCategory
            val amount = _uiState.value.amount.toIntOrNull() ?: 0

            if (current.amount.isBlank()) {
                _uiEvents.send(
                    ActionsUiEvent.Error("Montant invalide")
                )
                return@launch
            }

            when (current.type) {

                ActionType.ADD -> {

                    val source = _uiState.value.sourceCategory
                        ?: return@launch

                    val amountString = _uiState.value.amount

                    if (amountString.isBlank()) {
                        return@launch
                    }

                    val amount = amountString.toIntOrNull()

                    if (amount == null || amount <= 0) {
                        return@launch
                    }

                    val date = _uiState.value.date

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                    val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)

                    transactionRepository.insertTransaction(
                        TransactionActionEntity(
                            id = 0,
                            icon = "Add",
                            title = "Ajout",
                            description = "Ajout vers : ${source.name}",
                            amount = "+ $amount",
                            amountColor = Color.Green.toColorLong(),
                            date = "${dateFormatter.format(date)} • ${timeFormatter.format(date)}",
                            amountInt = amount,
                            dateInfo = date.time,
                            categorySourceId = source.id
                        )
                    )
                }

                ActionType.WITHDRAW -> {

                    val source = _uiState.value.sourceCategory
                        ?: return@launch

                    val amountString = _uiState.value.amount

                    if (amountString.isBlank()) {
                        return@launch
                    }

                    val amount = amountString.toIntOrNull()

                    if (amount == null || amount <= 0) {
                        return@launch
                    }

                    // Vérifie que la catégorie possède suffisamment d'argent
                    if (source.currentPrice < amount) {
                        return@launch
                    }

                    val date = _uiState.value.date

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                    val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)

                    transactionRepository.insertTransaction(
                        TransactionActionEntity(
                            id = 0,
                            icon = "Remove",
                            title = "Retrait",
                            description = "Retrait vers : ${source.name}",
                            amount = "- $amount",
                            amountColor = Color.Red.toColorLong(),
                            date = "${dateFormatter.format(date)} • ${timeFormatter.format(date)}",
                            amountInt = -amount,
                            dateInfo = date.time,
                            categorySourceId = source.id
                        )
                    )
                }

                ActionType.TRANSFER -> {

                    val source = _uiState.value.sourceCategory
                        ?: return@launch

                    val dest = _uiState.value.destinationCategory
                        ?: return@launch

                    if (source.id == dest.id) {
                        return@launch
                    }

                    val amount = _uiState.value.amount.toIntOrNull()

                    if (amount == null || amount <= 0) {
                        return@launch
                    }

                    if (source.currentPrice < amount) {
                        return@launch
                    }

                    val date = _uiState.value.date

                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                    val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)

                    transactionRepository.insertTransaction(
                        TransactionActionEntity(
                            id = 0,
                            icon = "SwapHoriz",
                            title = "Transfert",
                            description = "${source.name} → ${dest.name}",
                            amount = "↪ $amount",
                            amountColor = Color.Yellow.toColorLong(),
                            date = "${dateFormatter.format(date)} • ${timeFormatter.format(date)}",
                            amountInt = amount,
                            dateInfo = date.time,
                            categorySourceId = source.id,
                            categoryDestId = dest.id
                        )
                    )
                }
            }
            _uiEvents.send(ActionsUiEvent.Back)
        }
    }
}