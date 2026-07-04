package com.example.theolaforgeeval.navhost

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.theolaforgeeval.ui.screen.Add.AddScreen
import com.example.theolaforgeeval.ui.screen.Add.AddViewModel
import com.example.theolaforgeeval.ui.screen.actions.ActionsScreen
import com.example.theolaforgeeval.ui.screen.actions.ActionsViewModel
import com.example.theolaforgeeval.ui.screen.home.HomeScreen
import com.example.theolaforgeeval.ui.screen.home.HomeViewModel
import com.example.theolaforgeeval.ui.screen.details.DetailsScreen
import com.example.theolaforgeeval.ui.screen.details.DetailsViewModel
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
                    navController.navigate(Screen.Details.createRoute(id)) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->

            val id = backStackEntry.arguments!!.getInt("id")
            val viewModel: DetailsViewModel = koinViewModel()

            DetailsScreen(
                viewModel = viewModel,
                id = id,
                navController = navController
            )
        }

        composable(Screen.Add.route) {

            val viewModel: AddViewModel = koinViewModel()

            AddScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(Screen.Actions.route) {

            val viewModel: ActionsViewModel = koinViewModel()

            ActionsScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}