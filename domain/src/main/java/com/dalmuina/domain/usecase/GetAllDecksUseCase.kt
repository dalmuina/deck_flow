package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFDeck
import com.dalmuina.domain.model.DFError
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.flow.Flow

class GetAllDecksUseCase(
    private val repository: LocalDeckRepository
) {
    operator fun invoke(): Flow<DFResult<List<DFDeck>, DataBaseError>> =
        repository.getAllDecksWithCards()
}
