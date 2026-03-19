package com.dalmuina.domain

import com.dalmuina.domain.model.DFDeckDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    suspend fun createDeck(name: String, cardIds: List<Int>): DFResult<Unit, DataBaseError>

    suspend fun updateDeckName(deckId: Int, name: String): DFResult<Unit, DataBaseError>

    fun getAllDecksWithCards(): Flow<DFResult<List<DFDeckDomain>, DataBaseError>>

    fun getDeckWithCardsById(deckId: Int): Flow<DFResult<DFDeckDomain, DataBaseError>>

    suspend fun deleteDeck(deckId: Int): DFResult<Unit, DataBaseError>

    suspend fun setDeckCards(deckId: Int, orderedIds: List<Int>): DFResult<Unit, DataBaseError>

}