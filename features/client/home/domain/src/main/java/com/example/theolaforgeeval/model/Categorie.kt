package com.example.theolaforgeeval.model

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Categorie(
    val entity: CategoryEntity,
    val nom: String,
    val color: Color,
    val icon: ImageVector,
    val currentPrice: Int,
    val futurePrice: Int
)


