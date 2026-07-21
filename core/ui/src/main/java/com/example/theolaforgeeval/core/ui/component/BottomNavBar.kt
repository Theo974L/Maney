package com.example.theolaforgeeval.core.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 *
 * @property BottomNavScreen Dans le cas ou l'application évolue on a une class qui permet de naviguer dans l'application
 * @see BottomNavigationBar est un composant réutilisable
 * @param route c'est la route de l'écran
 * @param label c'est le label de l'écran
 * @param icon c'est l'icon de l'écran
 *
 */

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavScreen("home", "Accueil", Icons.Default.Home)
    object Add : BottomNavScreen("Add", "Ajouter", Icons.Default.Add)
    object Actions : BottomNavScreen("Actions", "Actions", Icons.Default.CompareArrows)
    object Settings : BottomNavScreen("Settings", "Réglages", Icons.Default.Settings)

}


@Composable
fun BottomNavigationBar(onItemClick: NavController) {
    val screens = listOf(
        BottomNavScreen.Actions,
        BottomNavScreen.Home,
        BottomNavScreen.Add,
        BottomNavScreen.Settings
    )

    val currentRoute = onItemClick.currentBackStackEntryAsState().value?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        screens.forEach { screen ->
            BottomNavItem(
                label = screen.label,
                icon = screen.icon,
                selected = screen.route == currentRoute
            ) {
                onItemClick.navigate(screen.route)
            }

        }

    }


}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {

    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (selected) 0.12f else 0f,
        label = "nav_item_bg"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = backgroundAlpha))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color)
        Text(
            label,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
