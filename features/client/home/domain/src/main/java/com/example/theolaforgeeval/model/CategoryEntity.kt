package com.example.theolaforgeeval.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Long,
    val iconName: String,
    val currentPrice: Double,
    val futurePrice: Double,
    val goalAmount: Double? = null,
    val imagePath: String? = null,
    val celebrated: Boolean = false
)