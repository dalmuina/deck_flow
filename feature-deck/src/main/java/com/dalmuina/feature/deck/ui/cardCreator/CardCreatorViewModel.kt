package com.dalmuina.feature.deck.ui.cardCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.UiEvent
import com.dalmuina.UiEventDispatcher
import com.dalmuina.domain.model.DFCard
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.SaveCardUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class CardCreatorViewModel(
    private val saveCardUseCase: SaveCardUseCase,
    private val uiEventDispatcher: UiEventDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardCreatorUiState())
    val uiState: StateFlow<CardCreatorUiState> = _uiState.asStateFlow()

    private val _events = Channel<CardCreatorEvent>()
    val events = _events.receiveAsFlow()

    fun process(intent: CardCreatorIntent) {
        when (intent) {
            is CardCreatorIntent.TitleChanged -> changeTitle(intent.value)
            is CardCreatorIntent.TimeChanged ->
                changeMinutes(intent.value)

            CardCreatorIntent.MoreTime ->
                adjustMinutes { it + 1 }

            CardCreatorIntent.LessTime ->
                adjustMinutes { it - 1 }

            is CardCreatorIntent.SaveActivity -> saveCard()
        }
    }

    private fun changeTitle(value: String) {
        reduce {
            copy(
                title = value
            )
        }
    }

    private fun changeMinutes(raw: String) {
        val filtered = raw.filter { it.isDigit() }
        val parsed = filtered.toLongOrNull() ?: 0
        reduce { copy(duration = parsed.minutes) }
    }

    private fun adjustMinutes(transform: (Long) -> Long) {
        reduce {
            val currentMinutes = duration.inWholeMinutes
            val newMinutes = transform(currentMinutes).coerceAtLeast(0)
            copy(duration = newMinutes.minutes)
        }
    }

    private fun saveCard() {
        viewModelScope.launch {
            val card = DFCard(
                title = _uiState.value.title,
                durationMillis = _uiState
                    .value
                    .duration.inWholeMilliseconds
            )
            saveCardUseCase(card)
                .onSuccess {
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar("Guardado con éxito")
                    )
                    _events.send(CardCreatorEvent.CloseScreen)


                }.onError {
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar("Error guardando la carta")
                    )
                }
        }
    }

    private inline fun reduce(
        reducer: CardCreatorUiState.() -> CardCreatorUiState
    ) {
        _uiState.update {
            it.reducer()
        }
    }

}