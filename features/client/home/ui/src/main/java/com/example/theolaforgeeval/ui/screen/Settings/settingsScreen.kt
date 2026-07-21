package com.example.theolaforgeeval.ui.screen.Settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.theolaforgeeval.core.ui.component.BottomNavigationBar
import com.example.theolaforgeeval.core.ui.component.ConfirmDialog
import com.example.theolaforgeeval.core.ui.component.IconSquare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    var pendingExportContent by remember { mutableStateOf<String?>(null) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val content = pendingExportContent
        pendingExportContent = null
        if (uri != null && content != null) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
                }
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportUri = uri
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ExportReady -> {
                    pendingExportContent = event.content
                    val fileName = "maney_backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.FRANCE).format(Date())}.json"
                    createDocumentLauncher.launch(fileName)
                }
                is SettingsError -> {
                    launch {
                        val job = launch {
                            snackBarHostState.showSnackbar(
                                message = event.message,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                        delay(1500)
                        job.cancel()
                        snackBarHostState.currentSnackbarData?.dismiss()
                    }
                }
                ImportSuccess -> {
                    launch {
                        snackBarHostState.showSnackbar(
                            message = "Import réussi",
                            duration = SnackbarDuration.Short
                        )
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
                text = "Réglages",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Sauvegarde", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "Exporte toutes tes données (catégories, objectifs, transactions, récurrences) dans un fichier, ou restaure une sauvegarde précédente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { viewModel.requestExport() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.width(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Exporter mes données")
                    }

                    OutlinedButton(
                        onClick = { openDocumentLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.width(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Importer des données")
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("Recurring") },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconSquare(
                        icon = Icons.Default.Repeat,
                        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                        tint = MaterialTheme.colorScheme.primary,
                        size = 44.dp,
                        cornerRadius = 14.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Récurrences", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Loyer, abonnements, salaire...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(">")
                }
            }
        }
    }

    pendingImportUri?.let { uri ->
        ConfirmDialog(
            title = "Importer ces données ?",
            message = "Cela remplacera TOUTES tes données actuelles (catégories, objectifs, transactions, récurrences). Cette action est irréversible.",
            confirmLabel = "Importer",
            onConfirm = {
                pendingImportUri = null
                scope.launch {
                    val content = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    }
                    if (content != null) {
                        viewModel.importContent(content)
                    }
                }
            },
            onDismiss = { pendingImportUri = null }
        )
    }
}
