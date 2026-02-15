package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class DeckCreatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DeckCreatorUiState())
    val uiState: StateFlow<DeckCreatorUiState> = _uiState.asStateFlow()

    fun process(intent: DeckCreatorIntent) {
        when (intent) {
            DeckCreatorIntent.Create -> createCard()
        }
    }

    private fun createCard() {
        reduce {
            copy(deckCard = this.deckCard + Random.nextInt(100).toString())
        }
    }


    private inline fun reduce(
        reducer: DeckCreatorUiState.() -> DeckCreatorUiState
    ) {
        _uiState.update {
            it.reducer()
        }
    }
}