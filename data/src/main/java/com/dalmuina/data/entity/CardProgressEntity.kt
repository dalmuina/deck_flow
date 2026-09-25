package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_progress",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class CardProgressEntity(
    @PrimaryKey
    val cardId: Int,
    val completedAt: Long? = null,
    val postponeAt: Long? = null,
)
