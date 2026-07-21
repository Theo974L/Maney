package com.example.theolaforgeeval.ui.screen.AddCategory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.IconSquare
import com.example.theolaforgeeval.ui.utils.categoryColors
import com.example.theolaforgeeval.ui.utils.categoryIcons
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel,
    navController: NavController,
    categoryId: Int? = null
) {
    val uiState = viewModel.state.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.loadForEdit(categoryId)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AddCategoryUiEvent.Error -> {
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
                is AddCategoryUiEvent.Back -> { navController.navigate("home") }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = if (uiState.isEditMode) "Modifier la catégorie" else "Nouvelle catégorie",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

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

            OutlinedTextField(
                value = uiState.name,
                onValueChange = {
                    if (it.length <= 25) {
                        viewModel.onAction(AddCategoryUiAction.OnNameChange(it))
                    }
                },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Text(
                "Couleur",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categoryColors) { color ->

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
                                viewModel.onAction(AddCategoryUiAction.OnColorSelect(color))
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

            Text(
                "Icône",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {

                items(categoryIcons) { icon ->

                    val selected = uiState.icon == icon

                    Card(
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                viewModel.onAction(AddCategoryUiAction.OnIconSelect(icon))
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

            Button(
                onClick = { viewModel.onAction(AddCategoryUiAction.OnSave) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (uiState.isEditMode) "Enregistrer" else "Créer la catégorie")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
