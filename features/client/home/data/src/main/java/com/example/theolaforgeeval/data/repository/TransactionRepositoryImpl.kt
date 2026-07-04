package com.example.theolaforgeeval.data.repository

import android.util.Log
import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.TransactionActionEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getTransactions() : Flow<List<TransactionActionEntity>> {
        return dao.getAll()
    }

    override suspend fun insertTransaction(transactionEntity: TransactionActionEntity) {
        Log.d("REPO", "INSERT DB = $transactionEntity")
        return dao.insert(transactionEntity)
    }
    //
    override suspend fun deleteTransaction(transactionEntity: TransactionActionEntity) {
        dao.delete(transactionEntity)
    }

    override fun getPastTransactions(timestamp: Long) : Flow<List<TransactionActionEntity>> {
        return dao.getPastTransactions(timestamp)
    }

    override fun getFutureTransactions(timestamp: Long,limite: Long) : Flow<List<TransactionActionEntity>> {
        return dao.getFutureTransactions(timestamp,limite)
    }



}