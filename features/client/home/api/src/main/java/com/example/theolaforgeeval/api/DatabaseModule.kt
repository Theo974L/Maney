package com.example.theolaforgeeval.api

import AppDatabase
import androidx.room.Room
import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.data.local.dao.RecurringTransactionDao
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DatabaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_db"
        )
            .fallbackToDestructiveMigration()
        .build()
    }

    single<CategoryDao> {
        get<AppDatabase>().categoryDao()
    }

    single<TransactionDao> {
        get<AppDatabase>().transactionDao()
    }

    single<RecurringTransactionDao> {
        get<AppDatabase>().recurringTransactionDao()
    }

}