package com.example.theolaforgeeval.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SegmentedOption(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun SegmentedToggle(
    options: List<SegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(index) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected)
                        option.color.copy(alpha = 0.15f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = if (selected) option.color else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) option.color else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
