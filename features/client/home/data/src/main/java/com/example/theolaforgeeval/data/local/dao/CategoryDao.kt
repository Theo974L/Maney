package com.example.theolaforgeeval.data.local.dao

import androidx.room.*
import com.example.theolaforgeeval.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Le DAO pour la bdd en local, permet d'effectuer des requêtes sur la bdd
 */

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("UPDATE categories SET currentPrice = :currentPrice and futurePrice = :futurePrice WHERE id = :id")
    suspend fun updateCurrentPrice(id: Int, currentPrice: Int, futurePrice :Int)
}