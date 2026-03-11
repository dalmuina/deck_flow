package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckRepository

class GetSelectedDeckUseCase(
    private val repository: SelectedDeckRepository
) {
    operator fun invoke() = repository.selectedDeckId
}
