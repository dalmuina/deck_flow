package com.dalmuina.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.dalmuina.domain.model.DFDeckDomain

data class DFDeckWithCards(

    @Embedded
    val deck: DFDeckEntity,

    @Relation(
        entity = DFCardEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DFDeckCardCrossEntity::class,
            parentColumn = "deckId",
            entityColumn = "cardId"
        )
    )
    val cards: List<DFCardWithProgress>
)

fun DFDeckWithCards.toDomain(): DFDeckDomain {
    return DFDeckDomain(
        id = deck.id,
        name = deck.name,
        cards = cards
            .map { it.toDomain()
            }
    )
}
