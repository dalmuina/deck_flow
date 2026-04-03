package com.dalmuina.core.test.data

import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFDeckDomain

object DeckDomainTestData {

    fun deck(
        id: Int = 1,
        name: String = "Deck $id",
        cards: List<DFCardDomain> = emptyList()
    ) = DFDeckDomain(
        id = id,
        name = name,
        cards = cards
    )

    fun decks(vararg ids: Int): List<DFDeckDomain> =
        ids.map { deck(id = it) }
}
