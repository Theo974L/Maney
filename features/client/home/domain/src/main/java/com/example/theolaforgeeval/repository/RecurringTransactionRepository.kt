package com.example.theolaforgeeval.repository

import com.example.theolaforgeeval.model.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

interface RecurringTransactionRepository {

    fun getAll(): Flow<List<RecurringTransactionEntity>>
    suspend fun getDue(now: Long): List<RecurringTransactionEntity>

    suspend fun insert(recurring: RecurringTransactionEntity)
    suspend fun update(recurring: RecurringTransactionEntity)
    suspend fun delete(recurring: RecurringTransactionEntity)
}
