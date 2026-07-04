package com.example.theolaforgeeval.data.repository

import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getCategories() : Flow<List<CategoryEntity>> {
        return dao.getAll()
    }

    override suspend fun insertCategories(categoryEntity: CategoryEntity) {

        return dao.insert(categoryEntity)
    }
//
    override suspend fun deleteCategories(category: CategoryEntity) {
        dao.delete(category)
    }

    override suspend fun updateCurrentPrice(id: Int, currentPrice: Int, futurePrice :Int){
        dao.updateCurrentPrice(id,currentPrice, futurePrice)
    }
}