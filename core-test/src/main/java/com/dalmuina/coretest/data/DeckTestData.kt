package com.dalmuina.coretest.data

import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFDeck

object DeckTestData {

    fun deck(
        id: Int = 1,
        name: String = "Deck $id",
        cards: List<DFCard> = emptyList()
    ) = DFDeck(
        id = id,
        name = name,
        cards = cards
    )

    fun decks(vararg ids: Int): List<DFDeck> =
        ids.map { deck(id = it) }
}
