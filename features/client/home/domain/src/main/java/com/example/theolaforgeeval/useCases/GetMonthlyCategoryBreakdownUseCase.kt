package com.example.theolaforgeeval.useCases

import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import kotlin.math.abs

data class CategoryBreakdown(
    val categoryId: Int,
    val name: String,
    val color: Long,
    val amount: Double,
    val fraction: Float
)

class GetMonthlyCategoryBreakdownUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(): Flow<List<CategoryBreakdown>> {

        return combine(
            transactionRepository.getTransactions(),
            categoryRepository.getCategories()
        ) { transactions, categories ->

            val monthStart = startOfCurrentMonthMillis()

            val amountsByCategory = transactions
                .filter { it.dateInfo >= monthStart && it.title != "Transfert" && it.amountValue < 0 }
                .groupBy { it.categorySourceId }
                .mapValues { (_, list) -> list.sumOf { abs(it.amountValue) } }

            val maxAmount = amountsByCategory.values.maxOrNull() ?: 0.0

            amountsByCategory.entries
                .mapNotNull { (categoryId, amount) ->
                    val category = categories.firstOrNull { it.id == categoryId } ?: return@mapNotNull null
                    CategoryBreakdown(
                        categoryId = categoryId,
                        name = category.name,
                        color = category.color,
                        amount = amount,
                        fraction = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f
                    )
                }
                .sortedByDescending { it.amount }
                .take(5)
        }
    }

    private fun startOfCurrentMonthMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
