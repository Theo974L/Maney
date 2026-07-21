package com.example.theolaforgeeval.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.theolaforgeeval.core.ui.component.GoalCard
import com.example.theolaforgeeval.core.ui.component.PriceInfoCard
import com.example.theolaforgeeval.model.CategoryEntity
import com.example.theolaforgeeval.ui.utils.Translate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    categories: List<CategoryEntity>,
    onSelect: (CategoryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = "Choisir une catégorie",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (categories.isEmpty()) {
                Text(
                    text = "Aucune catégorie pour le moment.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 480.dp)
                ) {
                    items(categories) { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(cat) }
                        ) {
                            val goalAmount = cat.goalAmount
                            if (goalAmount != null) {
                                GoalCard(
                                    icon = Translate.iconFromName(cat.iconName),
                                    imagePath = cat.imagePath,
                                    accentColor = Color(cat.color.toULong()),
                                    title = cat.name,
                                    currentPrice = cat.currentPrice,
                                    goalAmount = goalAmount
                                )
                            } else {
                                PriceInfoCard(
                                    icon = Translate.iconFromName(cat.iconName),
                                    iconBackground = Color(cat.color.toULong()),
                                    currentPrice = cat.currentPrice,
                                    futurePrice = cat.futurePrice,
                                    title = cat.name
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
