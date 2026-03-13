package com.dalmuina.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "deck_card_cross_ref",
    primaryKeys = ["deckId","cardId"],
    indices = [
        Index("deckId"),
        Index("cardId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = DFDeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DFCardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)

data class DFDeckCardCrossRef(
    val deckId: Int,
    val cardId: Int,
)
