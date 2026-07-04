package com.example.theolaforgeeval.api

import AppDatabase
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.data.repository.TransactionRepositoryImpl
import com.example.theolaforgeeval.repository.TransactionRepository
import org.koin.dsl.module

val TransactionModule = module {
    single<TransactionRepository> {
        TransactionRepositoryImpl(get())
    }
}