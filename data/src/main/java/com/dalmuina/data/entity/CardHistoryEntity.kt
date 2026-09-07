package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_history",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("cardId"),
        Index("dayStart"),
    ],
)
data class CardHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cardId: Int,
    val spentMillis: Long,
    val completedAt: Long,
    val dayStart: Long,
)
