package com.example.theolaforgeeval.navhost

/**
 * Cette class permet de répertorier les routes dans le même endroit
 *
 * @see Screen sert a afficher les differentes pages de l'application
 *
 * @see Home la page d'accueil
 *
 *
 */

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Add : Screen("Add")
    object AddCategory : Screen("AddCategory")
    object AddGoal : Screen("AddGoal")
    object Actions : Screen("Actions")
    object Settings : Screen("Settings")
    object Recurring : Screen("Recurring")

    object CategoryDetail : Screen("categoryDetail/{id}") {
        fun createRoute(id: Int) = "categoryDetail/$id"
    }

    object EditCategory : Screen("editCategory/{id}") {
        fun createRoute(id: Int) = "editCategory/$id"
    }

    object EditGoal : Screen("editGoal/{id}") {
        fun createRoute(id: Int) = "editGoal/$id"
    }

    object EditTransaction : Screen("editTransaction/{id}") {
        fun createRoute(id: Int) = "editTransaction/$id"
    }
}
