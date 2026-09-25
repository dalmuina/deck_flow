package com.dalmuina.domain

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.DeckDomain
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface DeckLocalDataSource {
    suspend fun createDeck(
        name: String,
        cardIds: List<Int>,
    ): DFResult<Int, DataError>

    suspend fun updateDeckName(
        deckId: Int,
        name: String,
    ): EmptyResult<DataError>

    fun getAllDecksWithCards(): Flow<DFResult<List<DeckDomain>, DataError>>

    fun getDeckWithCardsById(deckId: Int): Flow<DFResult<DeckDomain, DataError>>

    suspend fun deleteDeck(deckId: Int): EmptyResult<DataError>

    suspend fun setDeckCards(
        deckId: Int,
        orderedIds: List<Int>,
    ): EmptyResult<DataError>
}
