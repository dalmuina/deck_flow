package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckRepository
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.domain.model.PreferencesError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SetSelectedDeckUseCase(
    private val repository: SelectedDeckRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(id: Int): DFResult<Unit, PreferencesError> =
        withContext(dispatcher) {
            repository.setSelectedDeck(id)
        }
}
