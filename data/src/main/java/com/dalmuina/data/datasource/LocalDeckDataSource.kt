package com.dalmuina.data.datasource

import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.DFDeckEntity
import com.dalmuina.data.entity.DFDeckSummaryEntity
import com.dalmuina.data.entity.DeckWithCards
import kotlinx.coroutines.flow.Flow

class LocalDeckDataSource(
    private val dao: DFDeckDao
) {

    suspend fun createDeck(name: String, cardIds: Set<Int>) {
        dao.insertDeckWithCards(
            deck = DFDeckEntity(name = name),
            cardIds = cardIds
        )
    }

    suspend fun updateDeck(
        deckId: Int,
        name: String,
        cardIds: Set<Int>
    ) {
        dao.updateDeckWithCards(deckId, name, cardIds)
    }

    fun getDeckSummaries(): Flow<List<DFDeckSummaryEntity>> {
        return dao.getDeckSummaries()
    }

    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards> {
        return dao.getCardIdsForDeck(deckId)
    }
}
