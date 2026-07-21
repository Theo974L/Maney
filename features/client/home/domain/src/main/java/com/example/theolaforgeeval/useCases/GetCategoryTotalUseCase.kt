package com.example.theolaforgeeval.useCases

import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetCategoryTotalUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(): Flow<List<CategoryEntity>> {

        val today = System.currentTimeMillis()
        val nowPlusSevenDay = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)


        return combine(
            categoryRepository.getCategories(),
            transactionRepository.getPastTransactions(today),
            transactionRepository.getFutureTransactions(today,nowPlusSevenDay)
        ) { categories, transactions, transactionFuture ->

            categories.map { category ->

                val transfertPlus = transactions.filter {
                    it.categoryDestId == category.id
                }.sumOf {
                    it.amountValue
                }

                val transfertMoins = transactions.filter {
                    it.categorySourceId == category.id && it.title == "Transfert"
                }.sumOf {
                    it.amountValue
                }

                val total = transactions
                    .filter { it.categorySourceId == category.id && it.title != "Transfert" }
                    .sumOf { it.amountValue }

                val finalTotal = total + transfertPlus - transfertMoins

                val finalTotalPrevisionPlus = transactionFuture.filter {
                    it.categoryDestId == category.id
                }.sumOf {
                    it.amountValue
                }

                val finalTotalPrevisionMoins = transactionFuture.filter {
                    it.categorySourceId == category.id && it.title == "Transfert"
                }.sumOf {
                    it.amountValue
                }

                val TotalPrevision = transactionFuture
                    .filter { it.categorySourceId == category.id && it.title != "Transfert" }
                    .sumOf { it.amountValue }

                val finalTotalPrevision = TotalPrevision + finalTotalPrevisionPlus - finalTotalPrevisionMoins



                category.copy(
                    currentPrice = finalTotal,
                    futurePrice = finalTotalPrevision + finalTotal
                )
            }
        }
    }
}