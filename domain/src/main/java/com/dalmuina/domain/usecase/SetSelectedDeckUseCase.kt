package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SetSelectedDeckUseCase(
    private val repository: SelectedDeckRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Int) =
        withContext(dispatcher) {
            repository.setSelectedDeck(id)
        }
}
