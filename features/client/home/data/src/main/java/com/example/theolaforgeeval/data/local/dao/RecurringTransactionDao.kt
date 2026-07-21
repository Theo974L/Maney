package com.example.theolaforgeeval.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.theolaforgeeval.model.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {

    @Query("SELECT * FROM recurring_transaction ORDER BY nextOccurrence ASC")
    fun getAll(): Flow<List<RecurringTransactionEntity>>

    @Query("SELECT * FROM recurring_transaction WHERE active = 1 AND nextOccurrence <= :now")
    suspend fun getDue(now: Long): List<RecurringTransactionEntity>

    @Insert
    suspend fun insert(recurring: RecurringTransactionEntity)

    @Update
    suspend fun update(recurring: RecurringTransactionEntity)

    @Delete
    suspend fun delete(recurring: RecurringTransactionEntity)
}
