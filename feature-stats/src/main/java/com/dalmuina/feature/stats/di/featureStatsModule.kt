package com.dalmuina.feature.stats.di

import com.dalmuina.feature.stats.presentation.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureStatsModule = module {
    viewModel {
        StatsViewModel(
            getAllDecksUseCase = get(),
            getCardStatsUseCase = get(),
        )
    }
}