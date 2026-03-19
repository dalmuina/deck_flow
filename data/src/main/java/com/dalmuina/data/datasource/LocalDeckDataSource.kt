package com.dalmuina.data.datasource

import com.dalmuina.data.dao.DFDeckDao
import com.dalmuina.data.entity.DFCardInDeckEntity
import com.dalmuina.data.entity.DFDeckCardCrossEntity
import com.dalmuina.data.entity.DFDeckEntity
import com.dalmuina.data.entity.DFDeckWithCards
import kotlinx.coroutines.flow.Flow

class LocalDeckDataSource(
    private val dao: DFDeckDao
) {

    suspend fun createDeck(name: String, cardIds: List<Int>) {
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

    fun getAllDecksWithCards(): Flow<List<DFDeckWithCards>> =
        dao.getAllDecksWithCards()

    fun getDeck(deckId: Int): Flow<DFDeckEntity> =
        dao.getDeckById(deckId)

    fun getCardsForDeck(deckId: Int): Flow<List<DFCardInDeckEntity>> =
        dao.getCardsForDeck(deckId)

    suspend fun deleteDeck(deckId: Int) =
        dao.deleteDeck(deckId)

    suspend fun setDeckCards(deckId: Int, orderedIds: List<Int>) =
        dao.replaceDeckCards(deckId, orderedIds)

}
