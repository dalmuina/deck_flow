package com.dalmuina.domain

import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

interface LocalCardRepository {
    suspend fun saveCard(card: DFCard): DFResult<Int, DataBaseError>
    suspend fun updateCard(card: DFCard): DFResult<Int, DataBaseError>
    suspend fun completeCard(cardId: Int): DFResult<Int, DataBaseError>
    suspend fun postponeCard(cardId: Int): DFResult<Int, DataBaseError>

    fun getAllCards(): Flow<DFResult<List<DFCard>, DataBaseError>>
    suspend fun getCardById(cardId: Int): DFResult<DFCard, DataBaseError>

    suspend fun deleteCard(cardId: Int): DFResult<Unit, DataBaseError>
 }