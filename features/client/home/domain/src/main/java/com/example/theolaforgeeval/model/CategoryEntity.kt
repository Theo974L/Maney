package com.example.theolaforgeeval.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: Long,
    val iconName: String,
    val currentPrice: Int,
    val futurePrice: Int
)