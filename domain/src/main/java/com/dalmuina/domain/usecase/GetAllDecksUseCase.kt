package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeckSummary
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import kotlinx.coroutines.flow.Flow

class GetAllDecksUseCase(
    private val repository: LocalDeckRepository
) {
    operator fun invoke(): Flow<DFResult<List<DFDeckSummary>, DFError>> =
        repository.getAllDecks()
}
