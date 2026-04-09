package com.dalmuina.domain.usecase

import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.model.DeckDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.flow.Flow

class GetAllDecksUseCase(
    private val repository: DeckLocalDataSource
) {
    operator fun invoke(): Flow<DFResult<List<DeckDomain>, DataError>> =
        repository.getAllDecksWithCards()
}
