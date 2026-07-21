package com.example.theolaforgeeval.core.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.theolaforgeeval.core.ui.utils.formatEuro

@Composable
fun PriceInfoCard(
    icon: ImageVector,
    iconBackground: Color,
    currentPrice: Double,
    futurePrice: Double,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {

    val diff = futurePrice - currentPrice
    val isUp = diff >= 0.0

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "alpha"
    )

    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 6.dp,
        label = "offset"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY.toPx()
            }
            .let { m -> if (onClick != null) m.clickable { onClick() } else m },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICON PLUS PETIT
            Box(contentAlignment = Alignment.BottomEnd) {

                IconSquare(
                    icon = icon,
                    backgroundColor = iconBackground.copy(alpha = 0.12f),
                    tint = iconBackground,
                    size = 38.dp,
                    cornerRadius = 12.dp
                )

                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUp)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.error
                        )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = currentPrice.formatEuro(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Prévision ${futurePrice.formatEuro()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isUp)
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                                else
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isUp) "+${diff.formatEuro()}" else diff.formatEuro(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUp)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
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