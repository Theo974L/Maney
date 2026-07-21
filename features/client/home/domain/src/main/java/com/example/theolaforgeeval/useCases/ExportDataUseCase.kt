package com.example.theolaforgeeval.useCases

import com.example.theolaforgeeval.model.BackupData
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.RecurringTransactionRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExportDataUseCase(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository
) {
    private val json = Json { prettyPrint = true }

    suspend operator fun invoke(): String {
        val backup = BackupData(
            categories = categoryRepository.getCategories().first(),
            transactions = transactionRepository.getTransactions().first(),
            recurringTransactions = recurringTransactionRepository.getAll().first()
        )

        return json.encodeToString(backup)
    }
}
