package com.dalmuina.domain.usecase

import com.dalmuina.domain.CardLocalDataSource
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.flow.Flow

class GetAllCardsUseCase(
    private val repository: CardLocalDataSource
) {
    operator fun invoke(): Flow<DFResult<List<DFCardDomain>, DataError.Local>> =
        repository.getAllCards()
}
