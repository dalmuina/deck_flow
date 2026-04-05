package com.dalmuina.domain.usecase

import com.dalmuina.domain.SelectedDeckDataSource

class GetSelectedDeckUseCase(
    private val dataSource: SelectedDeckDataSource
) {
    operator fun invoke() = dataSource.selectedDeckId
}
