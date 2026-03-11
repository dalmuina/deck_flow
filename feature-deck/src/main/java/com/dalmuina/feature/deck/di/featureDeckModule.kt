package com.dalmuina.feature.deck.di

import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorMode
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorViewModel
import com.dalmuina.feature.deck.ui.deckCreator.DeckCreatorViewModel
import com.dalmuina.feature.deck.ui.deckCreator.DeckCreatorMode
import com.dalmuina.feature.deck.ui.deckSelector.DeckSelectorViewModel
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
            addCardToDeckUseCase = get(),
            removeCardFromDeckUseCase = get(),
            getDeckByIdUseCase = get(),
            deleteCardUseCase = get(),
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