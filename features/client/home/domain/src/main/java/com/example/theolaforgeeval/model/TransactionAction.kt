package com.example.theolaforgeeval.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionAction(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val amount: String,
    val amountColor: Color,
    val date: String,

    val dateInfo: Long,
    val amountInt: Int = 0,

    val categorySourceId: Int,

    val categoryDestId: Int? = null // 👈 IMPORTANT
)
