package com.dalmuina.feature.deck.presentation.deckSelector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.core.presentation.events.UiEvent
import com.dalmuina.core.presentation.events.UiEventDispatcher
import com.dalmuina.core.presentation.mappers.toUiText
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onFailure
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.DeleteDeckUseCase
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.SetSelectedDeckUseCase
import com.dalmuina.feature.deck.presentation.model.toUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeckSelectorViewModel(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val getSelectedDeckUseCase: GetSelectedDeckUseCase,
    private val setSelectedDeckUseCase: SetSelectedDeckUseCase,
    private val uiEventDispatcher: UiEventDispatcher,
) : ViewModel() {

    val uiState: StateFlow<DeckSelectorState> =
        combine(
            getAllDecksUseCase(),
            getSelectedDeckUseCase(),
        ) { decksResult, selectedIdResult ->

            when (decksResult) {
                is DFResult.Error -> {
                    DeckSelectorState(
                        loading = false,
                        deckList = emptyList()
                    )
                }

                is DFResult.Success -> {
                    val decks = decksResult.data.map { it.toUi() }

                    val selectedId = when (selectedIdResult) {
                        is DFResult.Success -> selectedIdResult.data
                        is DFResult.Error -> null
                    }

                    val validSelected = selectedId?.takeIf { id ->
                        decks.any { it.id == id }
                    }

                    val finalSelected =
                        validSelected ?: decks.firstOrNull()?.id?.also { id ->
                            viewModelScope.launch {
                                setSelectedDeckUseCase(id)
                                    .onFailure { error ->
                                        uiEventDispatcher.dispatch(
                                            UiEvent.ShowSnackBar(error.toUiText())
                                        )
                                    }
                            }
                        }

                    DeckSelectorState(
                        loading = false,
                        deckList = decks.map { deck ->
                            deck.copy(isSelected = deck.id == finalSelected)
                        }
                    )
                }
            }
        }
            .onStart {
                emit(DeckSelectorState(loading = true))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DeckSelectorState(loading = true)
            )

    fun process(intent: DeckSelectorIntent) {
        when (intent) {
            is DeckSelectorIntent.DeleteDeck -> deleteDeck(intent.deckId)
            is DeckSelectorIntent.SelectDeck -> {
                viewModelScope.launch {
                    setSelectedDeckUseCase(intent.deckId)
                        .onFailure { error ->
                            uiEventDispatcher.dispatch(
                                UiEvent.ShowSnackBar(error.toUiText())
                            )
                        }
                }
            }
        }
    }

    private fun deleteDeck(deckId: Int) {
        viewModelScope.launch {
            deleteDeckUseCase(deckId)
                .onSuccess { nextDeck ->
                    nextDeck?.let {
                        setSelectedDeckUseCase(it)
                            .onFailure { error ->
                                uiEventDispatcher.dispatch(
                                    UiEvent.ShowSnackBar(error.toUiText())
                                )
                            }
                    }
                }
                .onFailure { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiText())
                    )
                }
        }
    }
}
