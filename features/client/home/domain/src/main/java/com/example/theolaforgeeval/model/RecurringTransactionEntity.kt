package com.example.theolaforgeeval.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class RecurrenceFrequency {
    WEEKLY,
    MONTHLY
}

@Serializable
@Entity(
    tableName = "recurring_transaction",
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
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val amountValue: Double,
    val icon: String,
    val frequency: RecurrenceFrequency,
    val nextOccurrence: Long,
    val active: Boolean = true,

    val categorySourceId: Int,
    val categoryDestId: Int? = null
)
