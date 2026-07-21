package com.example.theolaforgeeval.ui.screen.Recurring

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.ConfirmDialog
import com.example.theolaforgeeval.core.ui.component.IconSquare
import com.example.theolaforgeeval.core.ui.component.SegmentedOption
import com.example.theolaforgeeval.core.ui.component.SegmentedToggle
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import com.example.theolaforgeeval.model.RecurrenceFrequency
import com.example.theolaforgeeval.ui.component.CategoryPickerSheet
import com.example.theolaforgeeval.ui.screen.actions.ActionType
import com.example.theolaforgeeval.ui.utils.Translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecurringScreen(viewModel: RecurringViewModel, navController: NavController) {
    val uiState by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var showCategoryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is RecurringError -> {
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
                text = "Récurrences",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "Génère automatiquement une transaction à intervalle régulier (loyer, abonnement, salaire...).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // FORM CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.onAction(OnTitleChange(it)) },
                        label = { Text("Nom (ex: Loyer)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = { viewModel.onAction(OnAmountChange(it)) },
                        label = { Text("Montant (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    SegmentedToggle(
                        options = listOf(
                            SegmentedOption("Ajout", Icons.Default.AddCircle, Color(0xFF22C55E)),
                            SegmentedOption("Retrait", Icons.Default.RemoveCircle, Color(0xFFEF4444))
                        ),
                        selectedIndex = if (uiState.type == ActionType.ADD) 0 else 1,
                        onSelect = { index ->
                            viewModel.onAction(OnTypeSelected(if (index == 0) ActionType.ADD else ActionType.WITHDRAW))
                        }
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryPicker = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Catégorie", fontWeight = FontWeight.Bold)
                                Text(uiState.category?.name ?: "Sélectionner")
                            }
                            Text(">")
                        }
                    }

                    SegmentedToggle(
                        options = listOf(
                            SegmentedOption("Hebdo", Icons.Default.DateRange, MaterialTheme.colorScheme.primary),
                            SegmentedOption("Mensuel", Icons.Default.CalendarMonth, MaterialTheme.colorScheme.secondary)
                        ),
                        selectedIndex = if (uiState.frequency == RecurrenceFrequency.WEEKLY) 0 else 1,
                        onSelect = { index ->
                            viewModel.onAction(
                                OnFrequencySelected(
                                    if (index == 0) RecurrenceFrequency.WEEKLY else RecurrenceFrequency.MONTHLY
                                )
                            )
                        }
                    )

                    Button(
                        onClick = { viewModel.onAction(OnAddRule) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Ajouter la récurrence")
                    }
                }
            }

            Text(
                text = "Récurrences actives",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            if (uiState.rules.isEmpty()) {
                Text(
                    text = "Aucune récurrence pour le moment.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.rules.forEach { rule ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconSquare(
                                    icon = Translate.iconFromName(rule.entity.icon),
                                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                    tint = MaterialTheme.colorScheme.primary,
                                    size = 40.dp,
                                    cornerRadius = 12.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(Modifier.weight(1f)) {
                                    Text(rule.entity.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${rule.categoryName} • ${if (rule.entity.frequency == RecurrenceFrequency.WEEKLY) "Toutes les semaines" else "Tous les mois"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Prochaine : ${rule.nextOccurrenceLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = (if (rule.entity.amountValue >= 0) "+" else "") + rule.entity.amountValue.formatEuro(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (rule.entity.amountValue >= 0)
                                        MaterialTheme.colorScheme.secondary
                                    else
                                        MaterialTheme.colorScheme.error
                                )

                                Switch(
                                    checked = rule.entity.active,
                                    onCheckedChange = {
                                        viewModel.onAction(OnToggleActive(rule.entity, it))
                                    }
                                )

                                IconButton(onClick = { viewModel.onAction(OnDeleteRuleClick(rule.entity)) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showCategoryPicker) {
        CategoryPickerSheet(
            categories = uiState.categories,
            onSelect = {
                viewModel.onAction(OnCategorySelected(it))
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    }

    uiState.pendingDelete?.let {
        ConfirmDialog(
            title = "Supprimer cette récurrence ?",
            message = "Les transactions déjà générées ne seront pas supprimées.",
            onConfirm = { viewModel.onAction(OnConfirmDeleteRule) },
            onDismiss = { viewModel.onAction(OnDismissDeleteRule) }
        )
    }
}
