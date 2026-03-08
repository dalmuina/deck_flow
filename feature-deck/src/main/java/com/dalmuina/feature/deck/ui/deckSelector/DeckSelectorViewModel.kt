package com.dalmuina.feature.deck.ui.deckSelector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.UiEventDispatcher
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorEvent
import com.dalmuina.feature.deck.ui.model.toDeckUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class DeckSelectorViewModel(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val uiEventDispatcher: UiEventDispatcher,
) : ViewModel() {

    private val _events = Channel<CardCreatorEvent>()
    val events = _events.receiveAsFlow()

    val uiState : StateFlow<DeckSelectorUiState> =
        getAllDecksUseCase()
            .map {result->
                when(result) {
                    is DFResult.Success -> {
                        DeckSelectorUiState(
                            loading = false,
                            deckList = result.data.map {it.toDeckUi()}
                        )
                    }
                    is DFResult.Error -> {
                        DeckSelectorUiState(
                            loading = false,
                            deckList = emptyList()
                        )
                    }
                }

            }
            .onStart {
                emit(DeckSelectorUiState(loading = true))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DeckSelectorUiState(loading = true)
            )
}
