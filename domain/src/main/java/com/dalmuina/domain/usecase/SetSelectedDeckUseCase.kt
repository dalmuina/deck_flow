package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckDataSource
import com.dalmuina.domain.model.DataError
import com.dalmuina.domain.model.EmptyResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SetSelectedDeckUseCase(
    private val repository: SelectedDeckDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Int): EmptyResult<DataError> =
        withContext(dispatcher) {
            repository.setSelectedDeck(id)
        }
}
