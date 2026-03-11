package com.dalmuina.data.datasource

import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.DFDeckCardCrossRef
import com.dalmuina.data.entity.DFDeckEntity
import com.dalmuina.data.entity.DFDeckSummaryEntity
import com.dalmuina.data.entity.DFDeckWithCards
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

    suspend fun updateDeckName(
        deckId: Int,
        name: String,
    ) {
        dao.updateDeckName(deckId, name)
    }

    suspend fun addCardToDeck(deckId: Int, cardId: Int) =
        dao.insertCrossRef(
            DFDeckCardCrossRef(
                deckId = deckId,
                cardId = cardId
            )
        )

    suspend fun removeCardFromDeck(deckId: Int, cardId: Int) =
        dao.deleteCrossRef(deckId, cardId)

    fun getDeckSummaries(): Flow<List<DFDeckSummaryEntity>> =
        dao.getDeckSummaries()

    fun getDeckWithCards(deckId: Int): Flow<DFDeckWithCards> =
        dao.getCardIdsForDeck(deckId)

    suspend fun deleteDeck(deckId: Int) =
        dao.deleteDeck(deckId)

}
