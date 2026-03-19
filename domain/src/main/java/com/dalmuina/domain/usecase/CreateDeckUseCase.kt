package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CreateDeckUseCase(
    private val repository: LocalDeckRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        name: String,
        cardIds: List<Int>,
    ): DFResult<Unit, DataBaseError> =
        withContext(dispatcher) {
            repository.createDeck(name, cardIds)
        }
}