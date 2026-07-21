package com.example.theolaforgeeval.ui.screen.AddGoal

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Savings
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.IconSquare
import com.example.theolaforgeeval.ui.utils.ImageStorage
import com.example.theolaforgeeval.ui.utils.categoryColors
import com.example.theolaforgeeval.ui.utils.categoryIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AddGoalScreen(
    viewModel: AddGoalViewModel,
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
                is AddGoalUiEvent.Error -> {
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
                is AddGoalUiEvent.Back -> { navController.navigate("home") }
            }
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val path = withContext(Dispatchers.IO) { ImageStorage.persist(context, uri) }
                if (path != null) {
                    viewModel.onAction(AddGoalUiAction.OnImageSelected(path))
                }
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
                text = if (uiState.isEditMode) "Modifier l'objectif" else "Nouvel objectif",
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

                    if (uiState.imagePath != null) {
                        AsyncImage(
                            model = File(uiState.imagePath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        IconSquare(
                            icon = uiState.icon,
                            backgroundColor = uiState.color,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (uiState.name.isBlank()) "Nom de l'objectif" else uiState.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = if (uiState.goalAmount.isNotBlank())
                                "Objectif : ${uiState.goalAmount} €"
                            else
                                "Aperçu en direct",
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
                        viewModel.onAction(AddGoalUiAction.OnNameChange(it))
                    }
                },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.goalAmount,
                onValueChange = { viewModel.onAction(AddGoalUiAction.OnGoalAmountChange(it)) },
                label = { Text("Montant objectif (€)") },
                leadingIcon = { Icon(Icons.Default.Savings, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PhotoCamera, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (uiState.imagePath != null) "Changer la photo" else "Ajouter une photo (optionnel)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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
                                viewModel.onAction(AddGoalUiAction.OnColorSelect(color))
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
                                viewModel.onAction(AddGoalUiAction.OnIconSelect(icon))
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
                onClick = { viewModel.onAction(AddGoalUiAction.OnSave) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (uiState.isEditMode) "Enregistrer" else "Créer l'objectif")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
