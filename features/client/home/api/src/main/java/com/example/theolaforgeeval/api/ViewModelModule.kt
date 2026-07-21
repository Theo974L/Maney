package com.example.theolaforgeeval.api

import com.example.theolaforgeeval.ui.screen.AddCategory.AddCategoryViewModel
import com.example.theolaforgeeval.ui.screen.AddGoal.AddGoalViewModel
import com.example.theolaforgeeval.ui.screen.CategoryDetail.CategoryDetailViewModel
import com.example.theolaforgeeval.ui.screen.Recurring.RecurringViewModel
import com.example.theolaforgeeval.ui.screen.Settings.SettingsViewModel
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
            getCategoryTotalUseCase = get(),
            generateDueRecurringTransactionsUseCase = get(),
            getMonthlyCategoryBreakdownUseCase = get()
        )
    }

    viewModel {
        AddCategoryViewModel(
            categoryRepository = get(),
        )
    }

    viewModel {
        AddGoalViewModel(
            categoryRepository = get(),
        )
    }

    viewModel {
        ActionsViewModel(
            transactionRepository = get(),
            getCategoryTotalUseCase = get()
        )
    }

    viewModel {
        CategoryDetailViewModel(
            getCategoryTotalUseCase = get(),
            transactionRepository = get(),
            categoryRepository = get(),
            deleteCategoryUseCase = get()
        )
    }

    viewModel {
        RecurringViewModel(
            recurringTransactionRepository = get(),
            getCategoryTotalUseCase = get()
        )
    }

    viewModel {
        SettingsViewModel(
            exportDataUseCase = get(),
            importDataUseCase = get()
        )
    }

}