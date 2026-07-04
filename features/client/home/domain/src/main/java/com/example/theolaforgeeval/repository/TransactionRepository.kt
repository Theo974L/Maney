package com.example.theolaforgeeval.repository

import com.example.theolaforgeeval.model.TransactionActionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    fun getTransactions() : Flow<List<TransactionActionEntity>>
    //    fun deleteCategories() : List<CategoryEntity>
    suspend fun insertTransaction(transactionEntity: TransactionActionEntity)
    suspend fun deleteTransaction(transactionEntity: TransactionActionEntity)

     fun getPastTransactions(timestamp: Long) : Flow<List<TransactionActionEntity>>
     fun getFutureTransactions(timestamp: Long,limite: Long) : Flow<List<TransactionActionEntity>>

}
