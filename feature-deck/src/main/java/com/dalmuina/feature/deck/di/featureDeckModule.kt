package com.dalmuina.feature.deck.di

import com.dalmuina.feature.deck.presentation.cardCreator.CardCreatorViewModel
import com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorViewModel
import com.dalmuina.feature.deck.presentation.deckSelector.DeckSelectorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureDeckModule = module{
    viewModelOf(::DeckSelectorViewModel)
    viewModelOf(::DeckCreatorViewModel)
    viewModelOf(::CardCreatorViewModel)
}