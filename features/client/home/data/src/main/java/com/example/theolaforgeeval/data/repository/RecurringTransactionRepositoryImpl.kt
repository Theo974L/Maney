package com.example.theolaforgeeval.data.repository

import com.example.theolaforgeeval.data.local.dao.RecurringTransactionDao
import com.example.theolaforgeeval.model.RecurringTransactionEntity
import com.example.theolaforgeeval.repository.RecurringTransactionRepository
import kotlinx.coroutines.flow.Flow

class RecurringTransactionRepositoryImpl(
    private val dao: RecurringTransactionDao
) : RecurringTransactionRepository {

    override fun getAll(): Flow<List<RecurringTransactionEntity>> {
        return dao.getAll()
    }

    override suspend fun getDue(now: Long): List<RecurringTransactionEntity> {
        return dao.getDue(now)
    }

    override suspend fun insert(recurring: RecurringTransactionEntity) {
        dao.insert(recurring)
    }

    override suspend fun update(recurring: RecurringTransactionEntity) {
        dao.update(recurring)
    }

    override suspend fun delete(recurring: RecurringTransactionEntity) {
        dao.delete(recurring)
    }
}
