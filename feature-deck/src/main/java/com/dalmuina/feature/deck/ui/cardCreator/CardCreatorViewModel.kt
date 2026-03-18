package com.dalmuina.feature.deck.ui.cardCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.designsystem.error.toUiMessage
import com.dalmuina.core.ui.UiEvent
import com.dalmuina.core.ui.UiEventDispatcher
import com.dalmuina.domain.model.DFCardDomain
import com.dalmuina.domain.model.onError
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

    private val _uiState = MutableStateFlow(CardCreatorUiState())
    val uiState: StateFlow<CardCreatorUiState> = _uiState.asStateFlow()

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
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
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
        val filtered = raw
            .filter(Char::isDigit)
            .toLongOrNull() ?: 0
        reduce { copy(duration = filtered.minutes) }
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
            val card = buildCard()
            val result = when (mode) {
                CardCreatorMode.Create ->
                    saveCardUseCase(card)
                is CardCreatorMode.Edit ->
                    updateCardUseCase(card)
            }
            result
                .onSuccess {cardId->
                    _events.emit(CardCreatorEvent.CloseScreen(cardId))
                }.onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }

    private fun buildCard(): DFCardDomain {
        val state = _uiState.value
        return DFCardDomain(
            id = mode.cardId ?: 0,
            name = state.name,
            durationMillis = state.duration.inWholeMilliseconds,
        )
    }

    private inline fun reduce(
        reducer: CardCreatorUiState.() -> CardCreatorUiState
    ) {
        _uiState.update {
            it.reducer()
        }
    }

}
