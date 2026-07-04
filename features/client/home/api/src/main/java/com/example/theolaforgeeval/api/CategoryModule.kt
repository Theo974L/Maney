package com.example.theolaforgeeval.api


import AppDatabase
import android.app.Application
import androidx.room.Room
import com.example.theolaforgeeval.data.local.dao.CategoryDao
import com.example.theolaforgeeval.data.local.dao.TransactionDao
import com.example.theolaforgeeval.data.repository.CategoryRepositoryImpl
import com.example.theolaforgeeval.data.repository.TransactionRepositoryImpl
import com.example.theolaforgeeval.repository.CategoryRepository
import com.example.theolaforgeeval.repository.TransactionRepository
import com.example.theolaforgeeval.ui.screen.Add.AddViewModel
import com.example.theolaforgeeval.ui.screen.actions.ActionsViewModel
import com.example.theolaforgeeval.ui.screen.home.HomeViewModel
import com.example.theolaforgeeval.useCases.DeleteCategoryUseCase
import com.example.theolaforgeeval.useCases.GetCategoryTotalUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * @properties PokemonModule c'est pour l'injetion de dependance de PokemonRepositoryImpl
 * @see PokemonModule est un singleton
 */

val CategoryModule = module {



    single<CategoryRepository> {
        CategoryRepositoryImpl(get())
    }

    single {
        DeleteCategoryUseCase(get())
    }

    single {
        GetCategoryTotalUseCase(get(),get())
    }

}