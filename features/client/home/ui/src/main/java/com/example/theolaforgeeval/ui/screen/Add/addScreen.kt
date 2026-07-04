package com.example.theolaforgeeval.ui.screen.Add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.IconSquare
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddScreen(viewModel: AddViewModel, navController: NavController) {
    val uiState = viewModel.state.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AddUiEvent.Error -> {
                    launch {
                        val job = launch {
                            snackBarHostState.showSnackbar(
                                message = event.message,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                        delay(1000)
                        job.cancel()
                        snackBarHostState.currentSnackbarData?.dismiss()
                    }
                }
                is AddUiEvent.Back -> { navController.navigate("home") }


            }
        }

    }

    Add(
        uiState,
        navController,
        onSave =  {
            viewModel.onAction(AddUiAction.OnSave)
        },
        onAction = viewModel::onAction
    )

}
@Composable
fun Add(
    uiState: AddUiState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    onAction: (AddUiAction) -> Unit

) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->

        AddContent(
            uiState,
            modifier = Modifier.padding(innerPadding),
            onSave = onSave,
            onAction = onAction

        )
    }
}
@Composable
private fun AddContent(
    uiState: AddUiState,
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
    onAction: (AddUiAction) -> Unit
) {

    var name by remember { mutableStateOf("") }

    var selectedColor by remember {
        mutableStateOf(Color(0xFFF5430B))
    }

    var selectedIcon by remember {
        mutableStateOf(Icons.Default.DirectionsCar)
    }

    val colors = listOf(
        Color(0xFFEF4444), // Rouge
        Color(0xFFF97316), // Orange
        Color(0xFFF59E0B), // Ambre
        Color(0xFFEAB308), // Jaune
        Color(0xFF84CC16), // Lime
        Color(0xFF22C55E), // Vert
        Color(0xFF10B981), // Emerald
        Color(0xFF14B8A6), // Teal
        Color(0xFF06B6D4), // Cyan
        Color(0xFF0EA5E9), // Sky
        Color(0xFF3B82F6), // Bleu
        Color(0xFF2563EB), // Bleu foncé
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Violet
        Color(0xFFA855F7), // Purple
        Color(0xFFD946EF), // Fuchsia
        Color(0xFFEC4899), // Rose
        Color(0xFFF43F5E), // Rose foncé
        Color(0xFF78716C), // Stone
        Color(0xFF6B7280), // Gray
        Color(0xFF374151), // Gray foncé
        Color(0xFF0F172A), // Slate
        Color(0xFF92400E), // Marron
        Color(0xFF15803D), // Vert forêt
        Color(0xFF1D4ED8), // Bleu royal
        Color(0xFF7C3AED), // Violet profond
        Color(0xFFBE123C), // Rouge bordeaux
        Color(0xFF0891B2), // Turquoise
        Color(0xFF4D7C0F), // Olive
        Color(0xFFC2410C)  // Cuivre
    )

    val icons = listOf(
        Icons.Default.DirectionsCar,
        Icons.Default.Flight,
        Icons.Default.Home,
        Icons.Default.Restaurant,
        Icons.Default.ShoppingCart,
        Icons.Default.LocalHospital,
        Icons.Default.Pets,
        Icons.Default.SportsSoccer,

        Icons.Default.Savings,
        Icons.Default.AttachMoney,
        Icons.Default.AccountBalance,
        Icons.Default.CreditCard,

        Icons.Default.School,
        Icons.Default.Book,
        Icons.Default.Work,
        Icons.Default.Business,

        Icons.Default.Movie,
        Icons.Default.MusicNote,
        Icons.Default.SportsEsports,
        Icons.Default.PhotoCamera,

        Icons.Default.FitnessCenter,
        Icons.Default.DirectionsBike,
        Icons.Default.DirectionsBus,
        Icons.Default.Train,

        Icons.Default.PhoneAndroid,
        Icons.Default.Computer,
        Icons.Default.Wifi,
        Icons.Default.Devices,

        Icons.Default.ChildCare,
        Icons.Default.FamilyRestroom,
        Icons.Default.Cake,
        Icons.Default.LocalCafe,

        Icons.Default.Forest,
        Icons.Default.Park,
        Icons.Default.LocalFlorist,
        Icons.Default.BeachAccess
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // TITLE (fixe)
        Text(
            text = "Nouvelle catégorie",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        // PREVIEW (fixe)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconSquare(
                    icon = uiState.icon,
                    backgroundColor = uiState.color,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (uiState.name.isBlank()) "Nom de catégorie" else uiState.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Aperçu en direct",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // INPUT FIXE
        OutlinedTextField(
            value = uiState.name,
            onValueChange = {
                if (it.length <= 25) {
                    onAction(AddUiAction.OnNameChange(it))
                }
            },
            label = { Text("Nom") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // COLORS FIXE (horizontal scroll OK)
        Text(
            "Couleur",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )


        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colors) { color ->

                val selected = uiState.color == color

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            2.dp,
                            if (selected) Color.White else Color.Transparent,
                            CircleShape
                        )
                        .clickable {
                            onAction(AddUiAction.OnColorSelect(color))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // IMPORTANT: ICONS SCROLL ONLY AREA
        Text(
            "Icône",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 👈 IMPORTANT: prend le reste de l'écran
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                items(icons) { icon ->

                    val selected = uiState.icon == icon

                    Card(
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                onAction(AddUiAction.OnIconSelect(icon))
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected)
                                uiState.color.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selected)
                                    uiState.color
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // BUTTON FIXE
        Button(
            onClick = { onAction(AddUiAction.OnSave) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Créer la catégorie")
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}