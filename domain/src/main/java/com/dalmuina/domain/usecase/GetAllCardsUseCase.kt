package com.dalmuina.domain.usecase

import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.flow.Flow

class GetAllCardsUseCase(
    private val repository: CardLocalDataSource,
) {
    operator fun invoke(): Flow<DFResult<List<CardDomain>, DataError>> = repository.getAllCards()
}
