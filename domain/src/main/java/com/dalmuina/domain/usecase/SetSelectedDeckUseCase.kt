package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckRepository
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SetSelectedDeckUseCase(
    private val repository: SelectedDeckRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Int): EmptyResult<DataError.Preferences> =
        withContext(dispatcher) {
            repository.setSelectedDeck(id)
        }
}
