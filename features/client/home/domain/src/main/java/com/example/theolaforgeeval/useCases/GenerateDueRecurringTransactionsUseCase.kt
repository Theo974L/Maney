package com.example.theolaforgeeval.useCases

import com.example.theolaforgeeval.model.RecurrenceFrequency
import com.example.theolaforgeeval.model.RecurringTransactionEntity
import com.example.theolaforgeeval.model.TransactionActionEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.RecurringTransactionRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val MAX_CATCH_UP_OCCURRENCES = 24

class GenerateDueRecurringTransactionsUseCase(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val due = recurringTransactionRepository.getDue(now)

        if (due.isEmpty()) return

        val categories = categoryRepository.getCategories().first()

        due.forEach { rule ->
            val category = categories.firstOrNull { it.id == rule.categorySourceId }
                ?: return@forEach

            var nextOccurrence = rule.nextOccurrence
            var iterations = 0

            while (nextOccurrence <= now && iterations < MAX_CATCH_UP_OCCURRENCES) {
                materialize(rule, category.color, nextOccurrence)
                nextOccurrence = advance(nextOccurrence, rule.frequency)
                iterations++
            }

            recurringTransactionRepository.update(rule.copy(nextOccurrence = nextOccurrence))
        }
    }

    private suspend fun materialize(rule: RecurringTransactionEntity, categoryColor: Long, occurrenceDate: Long) {
        val date = Date(occurrenceDate)
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)

        val amount = rule.amountValue
        val displayAmount = "%,.2f €".format(Locale.FRANCE, kotlin.math.abs(amount))

        transactionRepository.insertTransaction(
            TransactionActionEntity(
                id = 0,
                icon = rule.icon,
                title = "Récurrent",
                description = rule.title,
                amount = if (amount >= 0) "+ $displayAmount" else "- $displayAmount",
                amountColor = categoryColor,
                date = "${dateFormatter.format(date)} • ${timeFormatter.format(date)}",
                amountValue = amount,
                dateInfo = occurrenceDate,
                categorySourceId = rule.categorySourceId,
                categoryDestId = rule.categoryDestId
            )
        )
    }

    private fun advance(from: Long, frequency: RecurrenceFrequency): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = from
        when (frequency) {
            RecurrenceFrequency.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            RecurrenceFrequency.MONTHLY -> calendar.add(Calendar.MONTH, 1)
        }
        return calendar.timeInMillis
    }
}
