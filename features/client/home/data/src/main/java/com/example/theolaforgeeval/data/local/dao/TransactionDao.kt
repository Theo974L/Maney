package com.example.theolaforgeeval.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.model.TransactionActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transaction_action")
    fun getAll(): Flow<List<TransactionActionEntity>>

    @Query("SELECT * FROM transaction_action WHERE id = :id")
    fun getById(id: Int): Flow<TransactionActionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionAction: TransactionActionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactionActions: List<TransactionActionEntity>)

    @Update
    suspend fun update(transactionAction: TransactionActionEntity)

    @Delete
    suspend fun delete(transactionAction: TransactionActionEntity)

    @Query("DELETE FROM transaction_action")
    suspend fun deleteAll()

    @Query("""
    SELECT *
    FROM transaction_action
    WHERE dateInfo <= :now
    ORDER BY dateInfo DESC
""")
    fun getPastTransactions(now: Long): Flow<List<TransactionActionEntity>>

    @Query("""
    SELECT *
    FROM transaction_action
    WHERE dateInfo > :now and dateInfo <= :limite
    ORDER BY dateInfo ASC
""")
    fun getFutureTransactions(now: Long,limite: Long): Flow<List<TransactionActionEntity>>

    @Query("""
    SELECT *
    FROM transaction_action
    WHERE categorySourceId = :categoryId OR categoryDestId = :categoryId
    ORDER BY dateInfo DESC
""")
    fun getTransactionsForCategory(categoryId: Int): Flow<List<TransactionActionEntity>>

}