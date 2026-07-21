package com.example.theolaforgeeval.ui.screen.CategoryDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.ConfirmDialog
import com.example.theolaforgeeval.core.ui.component.IconSquare
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import com.example.theolaforgeeval.ui.screen.home.TransactionActionCard
import com.example.theolaforgeeval.ui.utils.Translate
import java.io.File

@Composable
fun CategoryDetailScreen(
    viewModel: CategoryDetailViewModel,
    categoryId: Int,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.onStart(categoryId)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NavigateBack -> navController.popBackStack()
                is NavigateToEdit -> {
                    val route = if (event.isGoal) "editGoal/${event.categoryId}" else "editCategory/${event.categoryId}"
                    navController.navigate(route)
                }
                is NavigateToEditTransaction -> {
                    navController.navigate("editTransaction/${event.transactionId}")
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        val category = uiState.category

        if (uiState.isLoading || category == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (category.imagePath != null) {
                            AsyncImage(
                                model = File(category.imagePath),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        } else {
                            IconSquare(
                                icon = category.icon,
                                backgroundColor = category.color,
                                tint = Color.White,
                                size = 56.dp,
                                cornerRadius = 16.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = category.nom,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val goalAmount = category.goalAmount
                    if (goalAmount != null) {
                        Text(
                            text = "${category.currentPrice.formatEuro()} / ${goalAmount.formatEuro()}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val progress = category.progress
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(category.color)
                            )
                        }
                    } else {
                        Text(
                            text = category.currentPrice.formatEuro(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prévision ${category.futurePrice.formatEuro()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.onAction(OnEditClick) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modifier")
                }

                Button(
                    onClick = { viewModel.onAction(OnDeleteCategoryClick) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Supprimer")
                }
            }

            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            if (uiState.transactions.isEmpty()) {
                Text(
                    text = "Aucune transaction pour le moment.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.transactions.forEach { transaction ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.onAction(OnTransactionClick(transaction)) }
                            ) {
                                TransactionActionCard(
                                    icon = Translate.iconFromName(transaction.icon),
                                    title = transaction.title,
                                    description = transaction.description,
                                    amount = transaction.amount,
                                    amountColor = Color(transaction.amountColor.toULong()),
                                    date = transaction.date
                                )
                            }

                            IconButton(onClick = { viewModel.onAction(OnDeleteTransactionClick(transaction)) }) {
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

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.showDeleteCategoryConfirm) {
            ConfirmDialog(
                title = if (category.isGoal) "Supprimer l'objectif ?" else "Supprimer la catégorie ?",
                message = "Cette action supprimera aussi tout l'historique de transactions associé. Cette action est irréversible.",
                onConfirm = { viewModel.onAction(OnConfirmDeleteCategory) },
                onDismiss = { viewModel.onAction(OnDismissDeleteCategory) }
            )
        }

        uiState.transactionPendingDelete?.let {
            ConfirmDialog(
                title = "Supprimer cette transaction ?",
                message = "Le solde de la catégorie sera automatiquement recalculé.",
                onConfirm = { viewModel.onAction(OnConfirmDeleteTransaction) },
                onDismiss = { viewModel.onAction(OnDismissDeleteTransaction) }
            )
        }
    }
}
