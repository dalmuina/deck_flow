package com.dalmuina.domain

import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import kotlinx.coroutines.flow.Flow

interface LocalCardRepository {

    suspend fun saveCard(card: DFCard): DFResult<Unit, DFError>

    fun getAllCards(): Flow<DFResult<List<DFCard>, DFError>>
}