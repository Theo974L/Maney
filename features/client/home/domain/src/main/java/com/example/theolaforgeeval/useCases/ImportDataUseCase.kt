package com.example.theolaforgeeval.useCases

import com.example.theolaforgeeval.model.BackupData
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.RecurringTransactionRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.serialization.json.Json

class ImportDataUseCase(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val recurringTransactionRepository: RecurringTransactionRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend operator fun invoke(content: String) {
        val backup = json.decodeFromString(BackupData.serializer(), content)

        transactionRepository.deleteAllTransactions()
        categoryRepository.deleteAllCategories()

        categoryRepository.insertAllCategories(backup.categories)
        transactionRepository.insertAllTransactions(backup.transactions)

        backup.recurringTransactions.forEach { rule ->
            recurringTransactionRepository.insert(rule)
        }
    }
}
