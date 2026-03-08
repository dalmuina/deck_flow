package com.dalmuina.domain

import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

interface LocalCardRepository {
    suspend fun saveCard(card: DFCard): DFResult<Unit, DataBaseError>
    suspend fun updateCard(card: DFCard): DFResult<Unit, DataBaseError>
    fun getAllCards(): Flow<DFResult<List<DFCard>, DFError>>

    suspend fun getCardById(cardId: Int): DFResult<DFCard, DataBaseError>
}