package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckDataSource

class GetSelectedDeckUseCase(
    private val repository: SelectedDeckDataSource
) {
    operator fun invoke() = repository.selectedDeckId
}
