package com.example.theolaforgeeval.ui.screen.actions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.PriceInfoCard
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.ui.utils.Translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ActionsScreen(viewModel: ActionsViewModel, navController: NavController) {
    val uiState = viewModel.state.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ActionsUiEvent.Error -> {
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
                is ActionsUiEvent.Back -> { navController.navigate("home") }


            }
        }

    }

    Actions(
        uiState,
        navController,
        onSave =  {
            viewModel.onAction(ActionsUiAction.OnSave)
        },
        onAction = viewModel::onAction
    )

}
@Composable
fun Actions(
    uiState: ActionsUiState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    onAction: (ActionsUiAction) -> Unit

) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->

        ActionsContent(
            uiState,
            modifier = Modifier.padding(innerPadding),
            onSave = onSave,
            onAction = onAction

        )
    }

}

@Composable
fun ActionsContent(
    uiState: ActionsUiState,
    modifier: Modifier,
    onAction: (ActionsUiAction) -> Unit,
    onSave: () -> Unit
) {
    var showSourcePicker by remember { mutableStateOf(false) }
    var showDestPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "Nouvelle opération",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text("Ajoutez, retirez ou transférez de l'argent")
            }
        }

        // TYPE SELECTOR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ActionChip(
                modifier = Modifier.weight(1f),
                title = "Ajout",
                icon = Icons.Default.AddCircle,
                color = Color(0xFF22C55E),
                selected = uiState.type == ActionType.ADD
            ) {
                onAction(ActionsUiAction.OnTypeSelected(ActionType.ADD))
            }

            ActionChip(
                modifier = Modifier.weight(1f),
                title = "Retrait",
                icon = Icons.Default.RemoveCircle,
                color = Color(0xFFEF4444),
                selected = uiState.type == ActionType.WITHDRAW
            ) {
                onAction(ActionsUiAction.OnTypeSelected(ActionType.WITHDRAW))
            }

            ActionChip(
                modifier = Modifier.weight(1f),
                title = "Transfert",
                icon = Icons.Default.SwapHoriz,
                color = Color(0xFFF59E0B),
                selected = uiState.type == ActionType.TRANSFER
            ) {
                onAction(ActionsUiAction.OnTypeSelected(ActionType.TRANSFER))
            }
        }

        // SOURCE CATEGORY
        CategoryBox(
            title = when (uiState.type) {
                ActionType.TRANSFER -> "Depuis"
                else -> "Catégorie"
            },
            category = uiState.sourceCategory,
            onClick = { showSourcePicker = true  }
        )

        // DESTINATION (TRANSFER ONLY)
        if (uiState.type == ActionType.TRANSFER) {
            CategoryBox(
                title = "Vers",
                category = uiState.destinationCategory,
                onClick = { showDestPicker = true  }
            )
        }

        DateField(uiState,onAction)
        // AMOUNT
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = {
                onAction(ActionsUiAction.OnAmountChange(it))
            },
            label = { Text("Montant") },
            leadingIcon = {
                Icon(Icons.Default.AttachMoney, null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        // SUMMARY CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {

                Text(
                    "Résumé",
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    when (uiState.type) {
                        ActionType.ADD -> "+${uiState.amount} €"
                        ActionType.WITHDRAW -> "-${uiState.amount} €"
                        ActionType.TRANSFER -> "${uiState.sourceCategory?.name ?: "?"} → ${uiState.destinationCategory?.name ?: "?"}"
                    }
                )
            }
        }

        // BUTTON
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                when (uiState.type) {
                    ActionType.ADD -> "Ajouter"
                    ActionType.WITHDRAW -> "Retirer"
                    ActionType.TRANSFER -> "Transférer"
                }
            )
        }
    }

    // PICKER SOURCE
    if (showSourcePicker) {
        CategoryPicker(
            categories = uiState.categories,
            onSelect = {
                onAction(ActionsUiAction.OnSourceCategorySelected(it))
                showSourcePicker = false
            },
            onDismiss = { showSourcePicker = false }
        )
    }

    // PICKER DEST
    if (showDestPicker) {
        CategoryPicker(
            categories = uiState.categories,
            onSelect = {
                onAction(ActionsUiAction.OnDestinationCategorySelected(it))
                showDestPicker = false
            },
            onDismiss = { showDestPicker = false }
        )
    }
}

@Composable
fun ActionChip(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) color.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                null,
                tint = if (selected) color else Color.Gray
            )
            Text(title)
        }
    }
}

@Composable
fun CategoryBox(
    title: String,
    category: CategoryEntity?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val catInfo = "${category?.name} - ${category?.currentPrice}€"
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    text = if (category?.name.isNullOrBlank()) "Sélectionner" else catInfo
                )
            }

            Text(">")
        }
    }
}


@Composable
fun CategoryPicker(
    categories: List<CategoryEntity>,
    onSelect: (CategoryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text("Choisir une catégorie")

            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(categories) { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(cat) }
                            .padding(12.dp)
                    ) {
                        PriceInfoCard(
                            Translate.iconFromName(cat.iconName),
                            Color(cat.color.toULong()),
                            currentPrice = cat.currentPrice,
                            futurePrice = cat.futurePrice,
                            title = cat.name,
                            onDelete = {

                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Fermer",
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    uiState: ActionsUiState,
    onAction: (ActionsUiAction) -> Unit
) {

    var showPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val interactionSource = remember { MutableInteractionSource() }

    val formatter = java.text.SimpleDateFormat(
        "dd/MM/yyyy",
        java.util.Locale.FRANCE
    )

    OutlinedTextField(
        value = formatter.format(uiState.date),
        onValueChange = {},
        readOnly = true,
        enabled = false, // IMPORTANT
        label = { Text("Date") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                showPicker = true
            },
        interactionSource = interactionSource,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onAction(
                                ActionsUiAction.OnDateChange(
                                    java.util.Date(millis)
                                )
                            )
                        }
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}