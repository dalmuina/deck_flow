package com.dalmuina.feature.deck.presentation.cardCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.core.presentation.events.UiEvent
import com.dalmuina.core.presentation.events.UiEventDispatcher
import com.dalmuina.core.presentation.mappers.toUiText
import com.dalmuina.domain.model.CardDomain
import com.dalmuina.domain.model.onFailure
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.GetCardByIdUseCase
import com.dalmuina.domain.usecase.SaveCardUseCase
import com.dalmuina.domain.usecase.UpdateCardUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class CardCreatorViewModel(
    private val mode: CardCreatorMode,
    private val saveCardUseCase: SaveCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val getCardByIdUseCase: GetCardByIdUseCase,
    private val uiEventDispatcher: UiEventDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardCreatorState(
        loading = mode is CardCreatorMode.Edit
    ))
    val uiState: StateFlow<CardCreatorState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CardCreatorEvent>()
    val events = _events.asSharedFlow()

    init {
        mode.cardId?.let {
            loadCard(it)
        }
    }

    fun process(intent: CardCreatorIntent) {
        when (intent) {
            is CardCreatorIntent.NameChanged -> changeName(intent.value)
            is CardCreatorIntent.TimeChanged ->
                changeMinutes(intent.value)

            CardCreatorIntent.MoreTime ->
                adjustMinutes { it + 1 }

            CardCreatorIntent.LessTime ->
                adjustMinutes { it - 1 }

            is CardCreatorIntent.SaveActivity -> saveCard()
            CardCreatorIntent.Cancel -> cancel()
        }
    }

    private fun loadCard(cardId: Int) {
        viewModelScope.launch {
            reduce {
                copy(
                    loading = true,
                )
            }
            getCardByIdUseCase(cardId)
                .onSuccess { card ->
                    reduce {
                        copy(
                            loading = false,
                            name = card.name,
                            duration = card.durationMillis.milliseconds
                        )
                    }
                }
                .onFailure { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiText())
                    )
                    reduce {
                        copy(
                            loading = false,
                        )
                    }
                }
        }
    }

    private fun changeName(value: String) {
        reduce {
            copy(
                name = value
            )
        }
    }

    private fun changeMinutes(raw: String) {
        val filtered = raw.filter(Char::isDigit)
        val minutes = if (filtered.isBlank()) {
            0L
        } else {
            filtered.toLongOrNull() ?: 0L
        }
        reduce { copy(duration = minutes.minutes) }
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
            reduce { copy(processing = true) }
            val card = buildCard()
            val result = when (mode) {
                CardCreatorMode.Create ->
                    saveCardUseCase(card)

                is CardCreatorMode.Edit ->
                    updateCardUseCase(card)
            }
            result
                .onSuccess { cardId ->
                    _events.emit(CardCreatorEvent.CloseScreen(cardId))
                }.onFailure { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiText())
                    )
                    reduce { copy(processing = false) }
                }
        }
    }

    private fun buildCard(): CardDomain {
        val state = _uiState.value
        return CardDomain(
            id = mode.cardId ?: 0,
            name = state.name,
            durationMillis = state.duration.inWholeMilliseconds,
        )
    }

    private fun cancel() {
        viewModelScope.launch {
            reduce { copy(processing = true) }
            _events.emit(CardCreatorEvent.CloseScreen(null))
        }
    }

    private inline fun reduce(
        reducer: CardCreatorState.() -> CardCreatorState
    ) {
        _uiState.update {
            it.reducer()
        }
    }

}
