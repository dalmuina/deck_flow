package com.dalmuina.domain

import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFDeck
import com.dalmuina.domain.model.DFDeckSummary
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    suspend fun createDeck(name: String, cardIds: Set<Int>): DFResult<Unit, DataBaseError>

    suspend fun addCardToDeck(deckId: Int, cardId: Int): DFResult<Unit, DataBaseError>

    suspend fun removeCardFromDeck(deckId: Int, cardId: Int): DFResult<Unit, DataBaseError>

    suspend fun updateDeckName(deckId: Int, name: String): DFResult<Unit, DataBaseError>

    fun getAllDecks(): Flow<DFResult<List<DFDeckSummary>, DataBaseError>>

    fun getDeckById(deckId: Int): Flow<DFResult<DFDeck, DataBaseError>>

    suspend fun deleteDeck(deckId: Int): DFResult<Unit, DataBaseError>
}