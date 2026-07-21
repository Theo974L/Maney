package com.example.theolaforgeeval.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val categories: List<CategoryEntity>,
    val transactions: List<TransactionActionEntity>,
    val recurringTransactions: List<RecurringTransactionEntity> = emptyList()
)
