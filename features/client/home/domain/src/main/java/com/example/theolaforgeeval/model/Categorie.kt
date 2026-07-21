package com.example.theolaforgeeval.model

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Categorie(
    val entity: CategoryEntity,
    val nom: String,
    val color: Color,
    val icon: ImageVector,
    val currentPrice: Double,
    val futurePrice: Double,
    val goalAmount: Double? = null,
    val imagePath: String? = null
) {
    val isGoal: Boolean get() = goalAmount != null

    val progress: Float
        get() {
            val goal = goalAmount ?: return 0f
            if (goal <= 0) return 0f
            return (currentPrice.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        }
}


