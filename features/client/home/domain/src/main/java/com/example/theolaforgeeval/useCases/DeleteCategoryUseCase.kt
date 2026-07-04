package com.example.theolaforgeeval.useCases

import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.repository.CategoryRepository

class DeleteCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: CategoryEntity) {
        repository.deleteCategories(category)
    }
}