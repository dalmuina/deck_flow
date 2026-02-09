package com.dalmuina.feature.deck.di

import com.dalmuina.feature.deck.ui.deckCreator.DeckCreatorViewModel
import com.dalmuina.feature.deck.ui.deckSelector.DeckSelectorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureDeckModule = module{
    viewModel {
        DeckSelectorViewModel()
    }
    viewModel {
        DeckCreatorViewModel()
    }
}