package com.example.theolaforgeeval.api

import com.example.theolaforgeeval.ui.screen.Add.AddViewModel
import com.example.theolaforgeeval.ui.screen.actions.ActionsViewModel
import com.example.theolaforgeeval.ui.screen.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ViewModuleModule = module {

    viewModel {
        HomeViewModel(
            categoryRepository = get(),
            deleteCategoryUseCase = get(),
            transactionRepository = get(),
            getCategoryTotalUseCase = get()
        )
    }

    viewModel {
        AddViewModel(
            categoryRepository = get(),
        )
    }

    viewModel {
        ActionsViewModel(
            transactionRepository = get(),
            getCategoryTotalUseCase = get()
        )
    }

}