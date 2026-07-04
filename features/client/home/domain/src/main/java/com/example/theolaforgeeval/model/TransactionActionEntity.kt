package com.example.theolaforgeeval.model

import androidx.room.Entity;
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey;

@Entity(
    tableName = "transaction_action",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categorySourceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryDestId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categorySourceId"),
        Index("categoryDestId")
    ]
)
data class TransactionActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val icon: String,
    val title: String,
    val description: String,
    val dateInfo: Long,
    val amount: String,
    val amountColor: Long,
    val amountInt: Int = 0,
    val date: String,

    val categorySourceId: Int,

    val categoryDestId: Int? = null // 👈 IMPORTANT
)