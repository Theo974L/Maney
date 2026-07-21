package com.example.theolaforgeeval.navhost

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.theolaforgeeval.ui.screen.Add.AddChooserScreen
import com.example.theolaforgeeval.ui.screen.AddCategory.AddCategoryScreen
import com.example.theolaforgeeval.ui.screen.AddCategory.AddCategoryViewModel
import com.example.theolaforgeeval.ui.screen.AddGoal.AddGoalScreen
import com.example.theolaforgeeval.ui.screen.AddGoal.AddGoalViewModel
import com.example.theolaforgeeval.ui.screen.actions.ActionsScreen
import com.example.theolaforgeeval.ui.screen.actions.ActionsViewModel
import com.example.theolaforgeeval.ui.screen.home.HomeScreen
import com.example.theolaforgeeval.ui.screen.home.HomeViewModel
import com.example.theolaforgeeval.ui.screen.CategoryDetail.CategoryDetailScreen
import com.example.theolaforgeeval.ui.screen.CategoryDetail.CategoryDetailViewModel
import com.example.theolaforgeeval.ui.screen.Recurring.RecurringScreen
import com.example.theolaforgeeval.ui.screen.Recurring.RecurringViewModel
import com.example.theolaforgeeval.ui.screen.Settings.SettingsScreen
import com.example.theolaforgeeval.ui.screen.Settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {

        composable(Screen.Home.route) {

            val viewModel: HomeViewModel = koinViewModel()

            HomeScreen(
                viewModel = viewModel,
                onNavigateDetails = { id ->
                    navController.navigate(Screen.CategoryDetail.createRoute(id))
                },
                navController = navController
            )
        }

        composable(
            route = Screen.CategoryDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments!!.getInt("id")
            val viewModel: CategoryDetailViewModel = koinViewModel()

            CategoryDetailScreen(
                viewModel = viewModel,
                categoryId = id,
                navController = navController
            )
        }

        composable(Screen.Add.route) {
            AddChooserScreen(navController = navController)
        }

        composable(Screen.AddCategory.route) {

            val viewModel: AddCategoryViewModel = koinViewModel()

            AddCategoryScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(Screen.AddGoal.route) {

            val viewModel: AddGoalViewModel = koinViewModel()

            AddGoalScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(
            route = Screen.EditCategory.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments!!.getInt("id")
            val viewModel: AddCategoryViewModel = koinViewModel()

            AddCategoryScreen(
                viewModel = viewModel,
                navController = navController,
                categoryId = id
            )
        }

        composable(
            route = Screen.EditGoal.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments!!.getInt("id")
            val viewModel: AddGoalViewModel = koinViewModel()

            AddGoalScreen(
                viewModel = viewModel,
                navController = navController,
                categoryId = id
            )
        }

        composable(Screen.Actions.route) {

            val viewModel: ActionsViewModel = koinViewModel()

            ActionsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments!!.getInt("id")
            val viewModel: ActionsViewModel = koinViewModel()

            ActionsScreen(
                viewModel = viewModel,
                navController = navController,
                transactionId = id
            )
        }

        composable(Screen.Recurring.route) {

            val viewModel: RecurringViewModel = koinViewModel()

            RecurringScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(Screen.Settings.route) {

            val viewModel: SettingsViewModel = koinViewModel()

            SettingsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}