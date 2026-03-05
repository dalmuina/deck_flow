package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.UiEvent
import com.dalmuina.UiEventDispatcher
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorEvent
import com.dalmuina.feature.deck.ui.model.toCardUi
import com.dalmuina.feature.deck.ui.model.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckCreatorViewModel(
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val createDeckUseCase: CreateDeckUseCase,
    private val uiEventDispatcher: UiEventDispatcher,
) : ViewModel() {

    private val _events = Channel<CardCreatorEvent>()
    val events = _events.receiveAsFlow()

    private val selectedCardIds = MutableStateFlow<Set<Int>>(emptySet())
    val uiState: StateFlow<DeckCreatorUiState> =
        combine(
            getAllCardsUseCase(),
            selectedCardIds
        ) { result, selectedIds ->

            when (result) {
                is DFResult.Success -> {

                    val cardsUi = result.data.map { card ->
                        val cardUi = card.toCardUi()
                        cardUi.copy(
                            isSelected = cardUi.id in selectedIds
                        )
                    }

                    DeckCreatorUiState(
                        loading = false,
                        deckCard = cardsUi
                    )
                }

                is DFResult.Error -> {
                    DeckCreatorUiState(
                        loading = false,
                        deckCard = emptyList()
                    )
                }
            }
        }
            .onStart {
                emit(DeckCreatorUiState(loading = true))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DeckCreatorUiState(loading = true)
            )

    fun process(intent: DeckCreatorIntent) {
        when (intent) {
            is DeckCreatorIntent.SelectedCard -> selectedCard(intent.id)
            DeckCreatorIntent.DeckCreated -> createDeck()
        }
    }

    private fun createDeck() {
        viewModelScope.launch {
            createDeckUseCase(name = "deck", cardIds = selectedCardIds.value)
                .onSuccess {
                    _events.send(CardCreatorEvent.CloseScreen)
                }
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }

    private fun selectedCard(id: Int) {
        selectedCardIds.update { current ->
            if (id in current) current - id
            else current + id
        }
    }

}