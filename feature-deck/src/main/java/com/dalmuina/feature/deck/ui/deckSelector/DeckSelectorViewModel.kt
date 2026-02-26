package com.dalmuina.feature.deck.ui.deckSelector

import androidx.lifecycle.ViewModel
import com.dalmuina.feature.deck.ui.model.DFDeckUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeckSelectorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DeckSelectorUiState())
    val uiState: StateFlow<DeckSelectorUiState> = _uiState.asStateFlow()

    fun process(intent: DeckSelectorIntent) {
        when (intent) {
            DeckSelectorIntent.LoadDecks -> loadDecks()
        }
    }

    private fun loadDecks() {
        reduce {
            copy(
                deckList = listOf(
                    DFDeckUi(
                        id = 0,
                        cardList = listOf(1),
                    ),
                    DFDeckUi(
                        id = 1,
                        cardList = listOf(1, 2, 3),
                    ),
                    DFDeckUi(
                        id = 2,
                        cardList = listOf(2, 34, 5, 2),
                    )
                )
            )
        }
    }

    private inline fun reduce(
        reducer: DeckSelectorUiState.() -> DeckSelectorUiState
    ) {
        _uiState.update {
            it.reducer()
        }
    }
}