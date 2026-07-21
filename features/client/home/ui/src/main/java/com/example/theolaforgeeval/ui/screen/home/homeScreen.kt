package com.example.theolaforgeeval.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.ConfettiOverlay
import com.example.theolaforgeeval.core.ui.component.ConfirmDialog
import com.example.theolaforgeeval.core.ui.component.GoalCard
import com.example.theolaforgeeval.core.ui.component.IconSquare
import com.example.theolaforgeeval.core.ui.component.PriceInfoCard
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import com.example.theolaforgeeval.model.Categorie
import com.example.theolaforgeeval.model.TransactionAction
import com.example.theolaforgeeval.useCases.CategoryBreakdown
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel, onNavigateDetails: (Int) -> Unit, navController: NavController) {
    val uiState by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is Error -> {
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

    Home(uiState,viewModel::onAction, onNavigateDetails, navController)

}

@Composable
private fun Home(
    uiState: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    onNavigateDetails: (Int) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            HomeHeroCard(total = uiState.total, futureTotal = uiState.futureTotal)

            if (uiState.monthlyBreakdown.isNotEmpty()) {
                MonthlyStatsCard(breakdown = uiState.monthlyBreakdown)
            }

            val isFullyEmpty = uiState.categories.isEmpty() && uiState.goals.isEmpty() && !uiState.isLoading

            if (isFullyEmpty) {
                WelcomeEmptyStateCard(onAddClick = { navController.navigate("Add") })
            } else {
                if (uiState.goals.isNotEmpty()) {
                    GoalsSection(goals = uiState.goals, onAction = onAction, onNavigateDetails = onNavigateDetails)
                }

                CategoriesSection(uiState = uiState, onAction = onAction, onNavigateDetails = onNavigateDetails)
            }

            ActionsSection("Activité Récente", icon = Icons.Default.History, actions = uiState.oldActions)

            ActionsSection("Activité Future", icon = Icons.Default.Schedule, actions = uiState.actions)
        }
    }
}

@Composable
fun HomeHeroCard(
    total: Double,
    futureTotal: Double,
    modifier: Modifier = Modifier
) {
    val diff = futureTotal - total
    val isUp = diff >= 0.0

    val cardShape = RoundedCornerShape(26.dp)

    val cardGradient = Brush.linearGradient(
        listOf(
            Color(0xFF0B1023),
            Color(0xFF1E1B4B),
            Color(0xFF3730A3)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.62f)
            .shadow(elevation = 20.dp, shape = cardShape, ambientColor = Color(0xFF3730A3), spotColor = Color(0xFF3730A3))
            .clip(cardShape)
            .background(cardGradient)
    ) {

        // Halo lumineux en haut à droite
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.14f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // Halo coloré en bas à gauche
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-70).dp, y = 70.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF818CF8).copy(alpha = 0.35f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // LIGNE HAUTE : puce + sans-contact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFDE68A), Color(0xFFF59E0B))
                            )
                        )
                )

                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(90f)
                )
            }

            // MILIEU : salutation + solde
            Column {
                Text(
                    text = "Solde actuel",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Text(
                    text = total.formatEuro(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }

            // LIGNE BASSE : marque + prévision
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "MANEY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {

                    Icon(
                        imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isUp) Color(0xFF4ADE80) else Color(0xFFF87171),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "/7j : ${futureTotal.formatEuro()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyStatsCard(
    breakdown: List<CategoryBreakdown>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = "Ce mois-ci",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(14.dp))

            breakdown.forEach { entry ->
                val barColor = Color(entry.color.toULong())

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(84.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(entry.fraction.coerceIn(0.05f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(barColor)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = entry.amount.formatEuro(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow_rotation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .rotate(rotation),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun WelcomeEmptyStateCard(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Savings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Bienvenue !",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crée ta première catégorie ou ton premier objectif d'épargne pour commencer à suivre ton budget.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ajouter")
            }
        }
    }
}

@Composable
fun CategoriesSection(
    uiState: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    onNavigateDetails: (Int) -> Unit
) {

    val categories = uiState.categories

    var expanded by remember {
        mutableStateOf(true)
    }

    var pendingDelete by remember { mutableStateOf<Categorie?>(null) }

    Column {

        SectionHeader(
            title = "Catégories",
            icon = Icons.Default.Category,
            count = categories.size,
            expanded = expanded,
            onToggle = { expanded = !expanded }
        )

        AnimatedVisibility(
            visible = expanded
        ) {

            Column {

                Spacer(modifier = Modifier.height(12.dp))

                if (categories.isEmpty()) {
                    EmptyStateCard(
                        message = "Aucune catégorie pour le moment. Ajoutes-en une depuis l'onglet Ajouter.",
                        icon = Icons.Default.Category
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                    ) {

                        items(categories) { category ->

                            PriceInfoCard(
                                icon = category.icon,
                                iconBackground = category.color,
                                title = category.nom,
                                currentPrice = category.currentPrice,
                                futurePrice = category.futurePrice,
                                onClick = { onNavigateDetails(category.entity.id) },
                                onDelete = { pendingDelete = category }
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { category ->
        ConfirmDialog(
            title = "Supprimer la catégorie ?",
            message = "Cette action supprimera aussi tout l'historique de transactions associé. Cette action est irréversible.",
            onConfirm = {
                onAction(OnClickDelete(categorie = category))
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}


@Composable
fun GoalsSection(
    goals: List<Categorie>,
    onAction: (HomeUiAction) -> Unit,
    onNavigateDetails: (Int) -> Unit
) {

    var expanded by remember {
        mutableStateOf(true)
    }

    var pendingDelete by remember { mutableStateOf<Categorie?>(null) }

    Column {

        SectionHeader(
            title = "Objectifs",
            icon = Icons.Default.Savings,
            count = goals.size,
            expanded = expanded,
            onToggle = { expanded = !expanded }
        )

        AnimatedVisibility(
            visible = expanded
        ) {

            Column {

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {

                    items(goals) { goal ->

                        val isReached = (goal.goalAmount ?: 0.0).let { it > 0.0 && goal.currentPrice >= it }
                        var showConfetti by remember(goal.entity.id) { mutableStateOf(false) }

                        LaunchedEffect(goal.entity.id, isReached) {
                            if (isReached && !goal.entity.celebrated) {
                                showConfetti = true
                                onAction(OnGoalCelebrated(goal))
                            }
                        }

                        Box {
                            GoalCard(
                                icon = goal.icon,
                                imagePath = goal.imagePath,
                                accentColor = goal.color,
                                title = goal.nom,
                                currentPrice = goal.currentPrice,
                                goalAmount = goal.goalAmount ?: 0.0,
                                onClick = { onNavigateDetails(goal.entity.id) },
                                onDelete = { pendingDelete = goal }
                            )

                            if (showConfetti) {
                                ConfettiOverlay(
                                    modifier = Modifier.matchParentSize(),
                                    onFinished = { showConfetti = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { goal ->
        ConfirmDialog(
            title = "Supprimer l'objectif ?",
            message = "Cette action supprimera aussi tout l'historique de transactions associé. Cette action est irréversible.",
            onConfirm = {
                onAction(OnClickDelete(categorie = goal))
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
fun TransactionActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    amount: String,
    amountColor: Color,
    date: String,
    modifier: Modifier = Modifier
) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 12.dp,
        label = "offset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = offsetY.toPx()
                this.alpha = alpha
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICON + DOT INDICATOR
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {

                IconSquare(
                    icon = icon,
                    backgroundColor = amountColor.copy(alpha = 0.12f),
                    tint = amountColor,
                    size = 40.dp,
                    cornerRadius = 14.dp
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(amountColor)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // TEXT BLOCK
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }

            // AMOUNT BADGE (PILL STYLE)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(amountColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                )
            }
        }
    }
}

@Composable
fun ActionsSection(
    title: String,
    icon: ImageVector,
    actions: List<TransactionAction>,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        SectionHeader(
            title = title,
            icon = icon,
            count = actions.size,
            expanded = expanded,
            onToggle = { expanded = !expanded }
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut())
        {

            Column {

                Spacer(modifier = Modifier.height(12.dp))

                val sortedActions = actions.sortedByDescending {
                    it.dateInfo
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 450.dp)
                ) {
                    items(sortedActions) { action ->
                        TransactionActionCard(
                            icon = action.icon,
                            title = action.title,
                            description = action.description,
                            amount = action.amount,
                            amountColor = action.amountColor,
                            date = action.date
                        )
                    }
                }
            }
        }
    }
}

