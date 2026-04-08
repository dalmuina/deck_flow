package com.dalmuina.core.test.data

import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DeckDomain

object DeckDomainTestData {

    fun deck(
        id: Int = 1,
        name: String = "Deck $id",
        cards: List<CardDomain> = emptyList()
    ) = DeckDomain(
        id = id,
        name = name,
        cards = cards
    )

    fun decks(vararg ids: Int): List<DeckDomain> =
        ids.map { deck(id = it) }
}
