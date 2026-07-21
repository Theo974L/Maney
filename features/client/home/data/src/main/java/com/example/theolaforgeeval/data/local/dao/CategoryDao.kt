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

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getById(id: Int): Flow<CategoryEntity?>

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}