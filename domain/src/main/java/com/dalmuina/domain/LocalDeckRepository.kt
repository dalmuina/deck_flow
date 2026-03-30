package com.dalmuina.domain

import com.dalmuina.domain.model.DFDeckDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.flow.Flow

interface LocalDeckRepository {

    suspend fun createDeck(name: String, cardIds: List<Int>): EmptyResult<DataError.Local>

    suspend fun updateDeckName(deckId: Int, name: String): EmptyResult<DataError.Local>

    fun getAllDecksWithCards(): Flow<DFResult<List<DFDeckDomain>, DataError.Local>>

    fun getDeckWithCardsById(deckId: Int): Flow<DFResult<DFDeckDomain, DataError.Local>>

    suspend fun deleteDeck(deckId: Int): EmptyResult<DataError.Local>

    suspend fun setDeckCards(deckId: Int, orderedIds: List<Int>): EmptyResult<DataError.Local>

}