package com.example.theolaforgeeval.core.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.theolaforgeeval.core.ui.utils.formatEuro
import java.io.File

@Composable
fun GoalCard(
    icon: ImageVector,
    imagePath: String?,
    accentColor: Color,
    title: String,
    currentPrice: Double,
    goalAmount: Double,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {

    val progress = if (goalAmount > 0) (currentPrice / goalAmount).toFloat().coerceIn(0f, 1f) else 0f
    val isReached = currentPrice >= goalAmount

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

    val animatedProgress by animateFloatAsState(
        targetValue = if (visible) progress else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY.toPx()
            }
            .let { m -> if (onClick != null) m.clickable { onClick() } else m },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(
            1.dp,
            if (isReached) accentColor.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                if (imagePath != null) {
                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    IconSquare(
                        icon = icon,
                        backgroundColor = accentColor.copy(alpha = 0.14f),
                        tint = accentColor,
                        size = 44.dp,
                        cornerRadius = 14.dp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${currentPrice.formatEuro()} / ${goalAmount.formatEuro()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (isReached) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Objectif atteint",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
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

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(accentColor.copy(alpha = 0.6f), accentColor)
                            )
                        )
                )
            }
        }
    }
}
