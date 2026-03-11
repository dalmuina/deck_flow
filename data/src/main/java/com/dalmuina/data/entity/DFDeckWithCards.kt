package com.dalmuina.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DFDeckWithCards(

    @Embedded
    val deck: DFDeckEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DFDeckCardCrossRef::class,
            parentColumn = "deckId",
            entityColumn = "cardId"
        )
    )
    val cards: List<DFCardEntity>
)
