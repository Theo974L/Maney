package com.example.theolaforgeeval.ui.screen.actions

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.theolaforgeeval.core.extensions.playSound
import com.example.theolaforgeeval.core.extensions.vibrate
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.SegmentedOption
import com.example.theolaforgeeval.core.ui.component.SegmentedToggle
import com.example.theolaforgeeval.core.ui.component.SuccessFeedbackOverlay
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import com.example.theolaforgeeval.features.client.home.ui.R
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.ui.utils.Translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ActionsScreen(
    viewModel: ActionsViewModel,
    navController: NavController,
    transactionId: Int? = null
) {
    val uiState = viewModel.state.collectAsState().value
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showSuccessOverlay by remember { mutableStateOf(false) }

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
                is ActionsUiEvent.Success -> {
                    if (event.playVibration) context.vibrate(80L)
                    if (event.playSound) context.playSound(R.raw.sound)

                    if (event.playAnimation) {
                        showSuccessOverlay = true
                    } else {
                        navController.navigate("home")
                    }
                }
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

    if (showSuccessOverlay) {
        SuccessFeedbackOverlay(
            message = when (uiState.type) {
                ActionType.ADD -> "Ajout effectué !"
                ActionType.WITHDRAW -> "Retrait effectué !"
                ActionType.TRANSFER -> "Transfert effectué !"
            },
            accent = typeColor(uiState.type),
            onFinished = {
                showSuccessOverlay = false
                navController.navigate("home")
            }
        )
    }
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

private fun typeColor(type: ActionType): Color = when (type) {
    ActionType.ADD -> Color(0xFF22C55E)
    ActionType.WITHDRAW -> Color(0xFFEF4444)
    ActionType.TRANSFER -> Color(0xFFF59E0B)
}

@Composable
fun ActionsContent(
    uiState: ActionsUiState,
    modifier: Modifier,
    onAction: (ActionsUiAction) -> Unit,
    onSave: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, label = "alpha")
    val offsetY by animateDpAsState(targetValue = if (visible) 0.dp else 12.dp, label = "offset")

    val accent = typeColor(uiState.type)
    val amount = uiState.amount.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
    val source = uiState.sourceCategory
    val dest = uiState.destinationCategory

    val canSave = when (uiState.type) {
        ActionType.TRANSFER -> source != null && dest != null && source.id != dest.id && amount > 0.0
        else -> source != null && amount > 0.0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY.toPx()
            },
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Text(
            text = if (uiState.isEditMode) "Modifier l'opération" else "Nouvelle opération",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

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

        // HERO AMOUNT
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Montant",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${uiState.amount.ifBlank { "0" }} €",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = accent
                ),
                textAlign = TextAlign.Center
            )

            if (amount > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                val preview = when (uiState.type) {
                    ActionType.ADD -> source?.let { "Nouveau solde : ${(it.currentPrice + amount).formatEuro()}" }
                    ActionType.WITHDRAW -> source?.let { "Nouveau solde : ${(it.currentPrice - amount).formatEuro()}" }
                    ActionType.TRANSFER -> {
                        if (source != null && dest != null)
                            "${source.name} ${(source.currentPrice - amount).formatEuro()} · ${dest.name} ${(dest.currentPrice + amount).formatEuro()}"
                        else null
                    }
                }
                if (preview != null) {
                    Text(
                        text = preview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // CATEGORY PICKER — SOURCE
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = when (uiState.type) {
                    ActionType.TRANSFER -> "Depuis"
                    else -> "Catégorie"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            CategoryAvatarRow(
                categories = uiState.categories,
                selectedId = source?.id,
                onSelect = { onAction(ActionsUiAction.OnSourceCategorySelected(it)) }
            )
        }

        // CATEGORY PICKER — DEST (TRANSFER ONLY)
        if (uiState.type == ActionType.TRANSFER) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Vers",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                CategoryAvatarRow(
                    categories = uiState.categories,
                    selectedId = dest?.id,
                    onSelect = { onAction(ActionsUiAction.OnDestinationCategorySelected(it)) }
                )
            }
        }

        // DATE PILL
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showDatePicker = true }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE).format(uiState.date),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // KEYPAD
        NumericKeypad(
            amount = uiState.amount,
            accent = accent,
            onAmountChange = { onAction(ActionsUiAction.OnAmountChange(it)) }
        )

        // CTA BUTTON
        Button(
            onClick = onSave,
            enabled = canSave,
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

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showDatePicker) {
        DateFieldDialog(
            initialDate = uiState.date,
            onConfirm = {
                onAction(ActionsUiAction.OnDateChange(it))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun CategoryAvatarRow(
    categories: List<CategoryEntity>,
    selectedId: Int?,
    onSelect: (CategoryEntity) -> Unit
) {
    if (categories.isEmpty()) {
        Text(
            text = "Aucune catégorie. Crées-en une depuis l'onglet Ajouter.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->

            val selected = category.id == selectedId
            val color = Color(category.color.toULong())

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(84.dp)
                    .clickable { onSelect(category) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = if (selected) 1f else 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (category.imagePath != null) {
                        AsyncImage(
                            model = File(category.imagePath),
                            contentDescription = category.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Translate.iconFromName(category.iconName),
                            contentDescription = category.name,
                            tint = if (selected) Color.White else color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = category.currentPrice.formatEuro(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun NumericKeypad(
    amount: String,
    accent: Color,
    onAmountChange: (String) -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(",", "0", "⌫")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    KeypadKey(
                        key = key,
                        accent = accent,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (key) {
                                "⌫" -> onAmountChange(amount.dropLast(1))
                                "," -> if (!amount.contains(",") && !amount.contains(".")) onAmountChange(amount + key)
                                else -> if (amount.length < 9) onAmountChange(amount + key)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(
    key: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1.6f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (key == "⌫") {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Effacer",
                    tint = accent
                )
            } else {
                Text(
                    text = key,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFieldDialog(
    initialDate: java.util.Date,
    onConfirm: (java.util.Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate.time)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onConfirm(java.util.Date(millis))
                    } ?: onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
