package com.dalmuina.feature.card.di

import com.dalmuina.feature.card.ui.CardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureCardModule = module{
    viewModel {
        CardViewModel()
    }
}