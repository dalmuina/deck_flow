package com.dalmuina.feature.deck.ui.deckSelector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.UiEvent
import com.dalmuina.UiEventDispatcher
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.DeleteDeckUseCase
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.SetSelectedDeckUseCase
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorEvent
import com.dalmuina.feature.deck.ui.model.toDeckUi
import com.dalmuina.feature.deck.ui.model.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeckSelectorViewModel(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val getSelectedDeckUseCase: GetSelectedDeckUseCase,
    private val setSelectedDeckUseCase: SetSelectedDeckUseCase,
    private val uiEventDispatcher: UiEventDispatcher,
) : ViewModel() {

    private val _events = Channel<CardCreatorEvent>()
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<DeckSelectorUiState> =
        combine(
            getAllDecksUseCase(),
            getSelectedDeckUseCase(),
        ) { result, selectedId ->

            when (result) {

                is DFResult.Success -> {

                    val decks = result.data.map { it.toDeckUi() }

                    val finalSelected =
                        selectedId ?: decks.firstOrNull()?.id

                    DeckSelectorUiState(
                        loading = false,
                        deckList = decks.map { deck ->
                            deck.copy(isSelected = deck.id == finalSelected)
                        }
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
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DeckSelectorUiState(loading = true)
            )

    fun process(intent: DeckSelectorIntent) {
        when (intent) {
            is DeckSelectorIntent.DeleteDeck -> deleteDeck(intent.deckId)
            is DeckSelectorIntent.SelectDeck -> {
                viewModelScope.launch {
                    setSelectedDeckUseCase(intent.deckId)
                }
            }
        }
    }

    private fun deleteDeck(deckId: Int) {
        viewModelScope.launch {
            val selectedId = getSelectedDeckUseCase().first()
            val nextDeck = takeIf { selectedId == deckId }
                ?.let { calculateNextDeck(deckId) }
            deleteDeckUseCase(deckId)
                .onSuccess {
                    nextDeck?.let { setSelectedDeckUseCase(it) }
                }
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }

    private fun calculateNextDeck(deletedId: Int): Int? {

        val decks = uiState.value.deckList.map { it.id }

        val index = decks.indexOf(deletedId)

        return when {
            index < decks.lastIndex -> decks[index + 1]
            index > 0 -> decks[index - 1]
            else -> null
        }
    }
}
