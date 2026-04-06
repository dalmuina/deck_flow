package com.dalmuina.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.dalmuina.domain.model.DeckDomain

data class DeckWithCards(

    @Embedded
    val deck: DeckEntity,

    @Relation(
        entity = CardEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = DeckCardCrossEntity::class,
            parentColumn = "deckId",
            entityColumn = "cardId"
        )
    )
    val cards: List<CardWithProgress>
)

fun DeckWithCards.toDomain(): DeckDomain {
    return DeckDomain(
        id = deck.id,
        name = deck.name,
        cards = cards
            .map { it.toDomain()
            }
    )
}
