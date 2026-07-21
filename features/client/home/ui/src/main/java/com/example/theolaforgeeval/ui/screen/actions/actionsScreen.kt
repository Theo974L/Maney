package com.example.theolaforgeeval.ui.screen.actions

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.SegmentedOption
import com.example.theolaforgeeval.core.ui.component.SegmentedToggle
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.ui.component.CategoryPickerSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ActionsScreen(
    viewModel: ActionsViewModel,
    navController: NavController,
    transactionId: Int? = null
) {
    val uiState = viewModel.state.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.loadForEdit(transactionId)
        }
    }

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

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, label = "alpha")
    val offsetY by animateDpAsState(targetValue = if (visible) 0.dp else 12.dp, label = "offset")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY.toPx()
            },
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
                    if (uiState.isEditMode) "Modifier l'opération" else "Nouvelle opération",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text("Ajoutez, retirez ou transférez de l'argent")
            }
        }

        // TYPE SELECTOR
        SegmentedToggle(
            options = listOf(
                SegmentedOption("Ajout", Icons.Default.AddCircle, Color(0xFF22C55E)),
                SegmentedOption("Retrait", Icons.Default.RemoveCircle, Color(0xFFEF4444)),
                SegmentedOption("Transfert", Icons.Default.SwapHoriz, Color(0xFFF59E0B))
            ),
            selectedIndex = when (uiState.type) {
                ActionType.ADD -> 0
                ActionType.WITHDRAW -> 1
                ActionType.TRANSFER -> 2
            },
            onSelect = { index ->
                val type = when (index) {
                    0 -> ActionType.ADD
                    1 -> ActionType.WITHDRAW
                    else -> ActionType.TRANSFER
                }
                onAction(ActionsUiAction.OnTypeSelected(type))
            }
        )

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        // SUMMARY CARD
        val amount = uiState.amount.trim().replace(',', '.').toDoubleOrNull() ?: 0.0

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
                        ActionType.ADD -> "+ ${amount.formatEuro()}"
                        ActionType.WITHDRAW -> "- ${amount.formatEuro()}"
                        ActionType.TRANSFER -> "${uiState.sourceCategory?.name ?: "?"} → ${uiState.destinationCategory?.name ?: "?"}"
                    }
                )

                val source = uiState.sourceCategory
                val dest = uiState.destinationCategory

                if (amount > 0.0) {
                    Spacer(Modifier.height(10.dp))

                    when (uiState.type) {
                        ActionType.ADD -> {
                            if (source != null) {
                                Text(
                                    text = "Nouveau solde : ${(source.currentPrice + amount).formatEuro()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        ActionType.WITHDRAW -> {
                            if (source != null) {
                                Text(
                                    text = "Nouveau solde : ${(source.currentPrice - amount).formatEuro()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        ActionType.TRANSFER -> {
                            if (source != null) {
                                Text(
                                    text = "${source.name} : ${(source.currentPrice - amount).formatEuro()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (dest != null) {
                                Text(
                                    text = "${dest.name} : ${(dest.currentPrice + amount).formatEuro()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
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
        CategoryPickerSheet(
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
        CategoryPickerSheet(
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
            val goalAmount = category?.goalAmount
            val currentPrice = category?.currentPrice
            val catInfo = if (goalAmount != null && currentPrice != null)
                "${category?.name} - ${currentPrice.formatEuro()} / ${goalAmount.formatEuro()}"
            else if (currentPrice != null)
                "${category?.name} - ${currentPrice.formatEuro()}"
            else
                "${category?.name}"
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
