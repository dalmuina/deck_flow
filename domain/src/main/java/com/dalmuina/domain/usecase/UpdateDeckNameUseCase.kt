package com.dalmuina.domain.usecase

import com.dalmuina.domain.LocalDeckRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class UpdateDeckNameUseCase(
    private val repository: LocalDeckRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        deckId: Int,
        name: String,
    ): DFResult<Unit, DataBaseError> =
        withContext(dispatcher) {
            repository.updateDeckName(deckId, name)
        }
}
