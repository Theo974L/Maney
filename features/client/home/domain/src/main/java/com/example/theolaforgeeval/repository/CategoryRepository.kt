package com.example.theolaforgeeval.repository

import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.CategoryEntity
import kotlinx.coroutines.flow.Flow


// Pour l'injection de dependance
interface CategoryRepository {
    fun getCategories() : Flow<List<CategoryEntity>>
//    fun deleteCategories() : List<CategoryEntity>
    suspend fun insertCategories(categoryEntity: CategoryEntity)

    suspend fun deleteCategories(categoryEntity: CategoryEntity)
    suspend fun updateCurrentPrice(id: Int, currentPrice: Int, futurePrice :Int)
}
