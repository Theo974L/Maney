package com.example.theolaforgeeval.data.repository

import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getCategories() : Flow<List<CategoryEntity>> {
        return dao.getAll()
    }

    override fun getCategoryById(id: Int): Flow<CategoryEntity?> {
        return dao.getById(id)
    }

    override suspend fun insertCategories(categoryEntity: CategoryEntity) {
        dao.insert(categoryEntity)
    }

    override suspend fun insertAllCategories(categories: List<CategoryEntity>) {
        dao.insertAll(categories)
    }

    override suspend fun updateCategory(categoryEntity: CategoryEntity) {
        dao.update(categoryEntity)
    }

    override suspend fun deleteCategories(category: CategoryEntity) {
        dao.delete(category)
    }

    override suspend fun deleteAllCategories() {
        dao.deleteAll()
    }
}
