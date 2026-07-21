package com.example.theolaforgeeval.repository

import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.CategoryEntity
import kotlinx.coroutines.flow.Flow


// Pour l'injection de dependance
interface CategoryRepository {
    fun getCategories() : Flow<List<CategoryEntity>>
    fun getCategoryById(id: Int): Flow<CategoryEntity?>

    suspend fun insertCategories(categoryEntity: CategoryEntity)
    suspend fun insertAllCategories(categories: List<CategoryEntity>)
    suspend fun updateCategory(categoryEntity: CategoryEntity)

    suspend fun deleteCategories(categoryEntity: CategoryEntity)
    suspend fun deleteAllCategories()
}
