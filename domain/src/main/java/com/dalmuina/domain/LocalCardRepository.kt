package com.dalmuina.domain

import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

interface LocalCardRepository {
    suspend fun saveCard(card: DFCardDomain): DFResult<Int, DataBaseError>
    suspend fun updateCard(card: DFCardDomain): DFResult<Int, DataBaseError>
    suspend fun completeCard(cardId: Int, spentMillis: Long): DFResult<Int, DataBaseError>
    suspend fun postponeCard(cardId: Int): DFResult<Int, DataBaseError>

    fun getAllCards(): Flow<DFResult<List<DFCardDomain>, DataBaseError>>
    suspend fun getCardById(cardId: Int): DFResult<DFCardDomain, DataBaseError>

    suspend fun deleteCard(cardId: Int): DFResult<Unit, DataBaseError>
 }