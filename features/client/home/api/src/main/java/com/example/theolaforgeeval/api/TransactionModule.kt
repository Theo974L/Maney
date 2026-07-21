package com.example.theolaforgeeval.api

import AppDatabase
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.data.repository.RecurringTransactionRepositoryImpl
import com.example.theolaforgeeval.data.repository.TransactionRepositoryImpl
import com.example.theolaforgeeval.repository.RecurringTransactionRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import com.example.theolaforgeeval.useCases.ExportDataUseCase
import com.example.theolaforgeeval.useCases.GenerateDueRecurringTransactionsUseCase
import com.example.theolaforgeeval.useCases.ImportDataUseCase
import org.koin.dsl.module

val TransactionModule = module {
    single<TransactionRepository> {
        TransactionRepositoryImpl(get())
    }

    single<RecurringTransactionRepository> {
        RecurringTransactionRepositoryImpl(get())
    }

    single {
        GenerateDueRecurringTransactionsUseCase(
            recurringTransactionRepository = get(),
            transactionRepository = get(),
            categoryRepository = get()
        )
    }

    single {
        ExportDataUseCase(
            categoryRepository = get(),
            transactionRepository = get(),
            recurringTransactionRepository = get()
        )
    }

    single {
        ImportDataUseCase(
            categoryRepository = get(),
            transactionRepository = get(),
            recurringTransactionRepository = get()
        )
    }
}