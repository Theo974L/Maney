package com.example.theolaforgeeval.repository

import com.example.theolaforgeeval.model.TransactionActionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getTransactions() : Flow<List<TransactionActionEntity>>
    fun getTransactionById(id: Int): Flow<TransactionActionEntity?>

    suspend fun insertTransaction(transactionEntity: TransactionActionEntity)
    suspend fun insertAllTransactions(transactions: List<TransactionActionEntity>)
    suspend fun updateTransaction(transactionEntity: TransactionActionEntity)
    suspend fun deleteTransaction(transactionEntity: TransactionActionEntity)
    suspend fun deleteAllTransactions()

    fun getPastTransactions(timestamp: Long) : Flow<List<TransactionActionEntity>>
    fun getFutureTransactions(timestamp: Long,limite: Long) : Flow<List<TransactionActionEntity>>
    fun getTransactionsForCategory(categoryId: Int): Flow<List<TransactionActionEntity>>

}
