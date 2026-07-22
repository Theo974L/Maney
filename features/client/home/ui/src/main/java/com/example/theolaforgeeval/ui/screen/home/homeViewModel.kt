package com.example.theolaforgeeval.ui.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.TransactionAction
import com.example.theolaforgeeval.model.TransactionActionEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import com.example.theolaforgeeval.ui.utils.Translate
import com.example.theolaforgeeval.useCases.DeleteCategoryUseCase
import com.example.theolaforgeeval.useCases.GenerateDueRecurringTransactionsUseCase
import com.example.theolaforgeeval.useCases.GetCategoryTotalUseCase
import com.example.theolaforgeeval.useCases.GetMonthlyCategoryBreakdownUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date


class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val transactionRepository: TransactionRepository,
    private val getCategoryTotalUseCase: GetCategoryTotalUseCase,
    private val generateDueRecurringTransactionsUseCase: GenerateDueRecurringTransactionsUseCase,
    private val getMonthlyCategoryBreakdownUseCase: GetMonthlyCategoryBreakdownUseCase

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _uiState

    private val _uiEvents = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _uiEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            generateDueRecurringTransactionsUseCase()
        }
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            getCategoryTotalUseCase()
                .collect { categories ->
                val mapped = categories.map { category ->
                    Categorie(
                        entity = category,
                        nom = category.name,
                        color = Color(category.color.toULong()),
                        icon = Translate.iconFromName(category.iconName),
                        currentPrice = category.currentPrice,
                        futurePrice = category.futurePrice,
                        goalAmount = category.goalAmount,
                        imagePath = category.imagePath
                    )
                }

                _uiState.value = _uiState.value.copy(
                    categories = mapped.filter { !it.isGoal },
                    goals = mapped.filter { it.isGoal },
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            val nowPlusOneDay = System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
            transactionRepository.getPastTransactions(nowPlusOneDay).collect { transaction ->
                val uiOldTransaction = transaction.map {
                    TransactionAction(
                        icon = Translate.iconFromName(it.icon),
                        title = it.title,
                        description = it.description,
                        amount = it.amount,
                        amountColor = Color(it.amountColor.toULong()),
                        date = it.date,
                        dateInfo = it.dateInfo,
                        amountValue = it.amountValue,
                        categorySourceId = it.categorySourceId,
                        categoryDestId = it.categoryDestId
                    )
                }

                _uiState.value = _uiState.value.copy(
                    oldActions = uiOldTransaction
                )
            }
        }

        viewModelScope.launch {
            val today = System.currentTimeMillis()
            val nowPlusSevenDay = System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)

            transactionRepository.getFutureTransactions(today,nowPlusSevenDay).collect { transaction ->


                val uiTransaction = transaction.map {
                    TransactionAction(
                        icon = Translate.iconFromName(it.icon),
                        title = it.title,
                        description = it.description,
                        amount = it.amount,
                        amountColor = Color(it.amountColor.toULong()),
                        date = it.date,
                        dateInfo = it.dateInfo,
                        amountValue = it.amountValue,
                        categorySourceId = it.categorySourceId,
                        categoryDestId = it.categoryDestId
                    )
                }

                _uiState.value = _uiState.value.copy(
                    actions = uiTransaction
                )
            }
        }

        viewModelScope.launch {

            transactionRepository.getTransactions().collect { transaction ->
                val today = System.currentTimeMillis()
                var sumOfPrice = 0.0
                val nowPlusSevenDay = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                var sumOfFuturePrice = 0.0


                transaction.forEach { t ->
                    if (t.dateInfo < today && t.title != "Transfert") {
                        sumOfPrice += t.amountValue
                    }
                }

                transaction.forEach { t ->
                    if (t.dateInfo < nowPlusSevenDay && t.title != "Transfert"){
                        sumOfFuturePrice += t.amountValue
                    }
                }

                _uiState.value = _uiState.value.copy(
                    total = sumOfPrice,
                    futureTotal = sumOfFuturePrice
                )

            }
        }

        viewModelScope.launch {
            getMonthlyCategoryBreakdownUseCase().collect { breakdown ->
                _uiState.value = _uiState.value.copy(
                    monthlyBreakdown = breakdown
                )
            }
        }


    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            is OnClickDelete -> {
                viewModelScope.launch {
                    deleteCategoryUseCase(action.categorie.entity)
                }
            }
            is OnGoalCelebrated -> {
                viewModelScope.launch {
                    categoryRepository.updateCategory(action.categorie.entity.copy(celebrated = true))
                }
            }
        }
    }

}

