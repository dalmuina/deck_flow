package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import com.dalmuina.feature.deck.ui.model.CardUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DeckCreatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DeckCreatorUiState())
    val uiState: StateFlow<DeckCreatorUiState> = _uiState.asStateFlow()

    fun process(intent: DeckCreatorIntent) {
        when (intent) {
            DeckCreatorIntent.Create -> createCard()
            is DeckCreatorIntent.CardClicked-> cardChanged(intent.id)
        }
    }

    private fun createCard() {
        reduce {
            copy(deckCard = this.deckCard + CardUi(
                id = UUID.randomUUID().toString(),
                title = "Test",
                duration = 0L.hours + 3L.minutes + 25L.seconds,
                false,
            ))
        }
    }

    private fun cardChanged(id: String){
        reduce {
            copy(
                deckCard = this.deckCard.map {
                    if (it.id == id) it.copy(isChecked = !it.isChecked)
                    else it
                }
            )
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