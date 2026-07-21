package com.example.theolaforgeeval.data.repository

import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.model.TransactionActionEntity
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun getTransactions() : Flow<List<TransactionActionEntity>> {
        return dao.getAll()
    }

    override fun getTransactionById(id: Int): Flow<TransactionActionEntity?> {
        return dao.getById(id)
    }

    override suspend fun insertTransaction(transactionEntity: TransactionActionEntity) {
        dao.insert(transactionEntity)
    }

    override suspend fun insertAllTransactions(transactions: List<TransactionActionEntity>) {
        dao.insertAll(transactions)
    }

    override suspend fun updateTransaction(transactionEntity: TransactionActionEntity) {
        dao.update(transactionEntity)
    }

    override suspend fun deleteTransaction(transactionEntity: TransactionActionEntity) {
        dao.delete(transactionEntity)
    }

    override suspend fun deleteAllTransactions() {
        dao.deleteAll()
    }

    override fun getPastTransactions(timestamp: Long) : Flow<List<TransactionActionEntity>> {
        return dao.getPastTransactions(timestamp)
    }

    override fun getFutureTransactions(timestamp: Long,limite: Long) : Flow<List<TransactionActionEntity>> {
        return dao.getFutureTransactions(timestamp,limite)
    }

    override fun getTransactionsForCategory(categoryId: Int): Flow<List<TransactionActionEntity>> {
        return dao.getTransactionsForCategory(categoryId)
    }
}
