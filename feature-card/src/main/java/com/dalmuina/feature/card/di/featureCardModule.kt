package com.dalmuina.feature.card.di

import com.dalmuina.feature.card.presentation.CardViewModel
import com.dalmuina.feature.card.presentation.TimerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureCardModule = module{
    viewModelOf(::CardViewModel)
    viewModelOf(::TimerViewModel)
}