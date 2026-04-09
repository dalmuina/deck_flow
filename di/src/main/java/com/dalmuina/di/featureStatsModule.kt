package com.dalmuina.di

import com.dalmuina.feature.stats.presentation.StatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureStatsModule = module {
    viewModelOf(::StatsViewModel)
}