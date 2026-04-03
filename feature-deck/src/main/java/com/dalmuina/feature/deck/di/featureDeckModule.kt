package com.dalmuina.feature.deck.di

import com.dalmuina.feature.deck.presentation.cardCreator.CardCreatorMode
import com.dalmuina.feature.deck.presentation.cardCreator.CardCreatorViewModel
import com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorViewModel
import com.dalmuina.feature.deck.presentation.deckCreator.DeckCreatorMode
import com.dalmuina.feature.deck.presentation.deckSelector.DeckSelectorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureDeckModule = module{
    viewModel {
        DeckSelectorViewModel(
            getAllDecksUseCase = get(),
            deleteDeckUseCase = get(),
            getSelectedDeckUseCase = get(),
            setSelectedDeckUseCase = get(),
            uiEventDispatcher = get(),
        )
    }
    viewModel {(mode: DeckCreatorMode)->
        DeckCreatorViewModel(
            mode = mode,
            getAllCardsUseCase = get(),
            createDeckUseCase = get(),
            updateDeckNameUseCase = get(),
            getDeckByIdUseCase = get(),
            deleteCardUseCase = get(),
            setDeckCardsUseCase = get(),
            uiEventDispatcher = get(),
        )
    }
    viewModel {(mode: CardCreatorMode)->
        CardCreatorViewModel(
            mode = mode,
            saveCardUseCase = get(),
            updateCardUseCase = get(),
            getCardByIdUseCase = get(),
            uiEventDispatcher = get(),
        )
    }
}