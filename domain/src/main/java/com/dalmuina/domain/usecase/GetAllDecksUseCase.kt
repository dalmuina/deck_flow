package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeckDomain
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

class GetAllDecksUseCase(
    private val repository: LocalDeckRepository
) {
    operator fun invoke(): Flow<DFResult<List<DFDeckDomain>, DataBaseError>> =
        repository.getAllDecksWithCards()
}
