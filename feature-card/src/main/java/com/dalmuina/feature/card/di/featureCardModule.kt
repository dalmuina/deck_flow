package com.dalmuina.feature.card.di

import com.dalmuina.feature.card.presentation.CardViewModel
import com.dalmuina.feature.card.presentation.TimerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureCardModule = module{
    viewModel {
        CardViewModel(
            getSelectedDeckUseCase = get(),
            getDeckByIdUseCase = get(),
            completeCardUseCase = get(),
            postponeCardUseCase = get(),
        )
    }
    viewModel {
        TimerViewModel(
            observeTimerStateUseCase = get(),
            saveTimerStateUseCase = get(),
        )
    }
}