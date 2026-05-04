package com.dalmuina.feature.card.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.CompleteCardUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.PostponeCardUseCase
import com.dalmuina.feature.card.model.CardCompletionPending
import com.dalmuina.feature.card.model.CardUi
import com.dalmuina.feature.card.model.SwipeDirection
import com.dalmuina.feature.card.model.toUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class CardViewModel(
    getSelectedDeckUseCase: GetSelectedDeckUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val completeCardUseCase: CompleteCardUseCase,
    private val postponeCardUseCase: PostponeCardUseCase,
) : ViewModel() {

    private val _sessionCards = MutableStateFlow<List<CardUi>>(emptyList())
    private val _completionPending = MutableStateFlow<CardCompletionPending?>(null)
    private var currentDeckId: Int? = null


    @OptIn(ExperimentalCoroutinesApi::class)
    val deckFlow =
        getSelectedDeckUseCase()
            .flatMapLatest { result ->
                when (result) {
                    is DFResult.Error -> flowOf(null)
                    is DFResult.Success -> {
                        val deckId = result.data
                        if (deckId == null) {
                            flowOf(null)
                        } else {
                            getDeckByIdUseCase(deckId)
                        }
                    }
                }
            }

    init {
        viewModelScope.launch {
            deckFlow.collectLatest { result ->

                if (result is DFResult.Success) {
                    val deckId = result.data.id
                    val dbCards = result.data.cards.map { it.toUi() }

                    val hasStructureChanged =
                        _sessionCards.value.map { it.id } != dbCards.map { it.id }

                    if (currentDeckId != deckId || hasStructureChanged) {
                        currentDeckId = deckId
                        _sessionCards.value = dbCards
                        return@collectLatest
                    }

                    val dbCardsById = dbCards.associateBy { it.id }

                    _sessionCards.update { current ->
                        current.map { card ->
                            dbCardsById[card.id] ?: card
                        }
                    }
                }
            }
        }
    }

    val uiState: StateFlow<CardState> =
        combine(deckFlow, _sessionCards, _completionPending) { result, session, pending ->
            when (result) {
                is DFResult.Success -> {
                    CardState(
                        loading = false,
                        name = result.data.name,
                        cards = session.filter { !it.isCompleted },
                        isDeckSelected = true,
                        completionPending = pending,
                    )
                }
                else -> {
                    CardState(
                        loading = false,
                        isDeckSelected = false,
                        completionPending = pending,
                    )
                }
            }
        }
            .onStart {
                emit(CardState(loading = true))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                CardState(loading = true)
            )

    fun process(intent: CardIntent) {
        when (intent) {
            is CardIntent.SwipeTopCard -> swipeTopCard(intent.direction)
            is CardIntent.RequestCompleteCard -> requestCompleteCard(intent.totalMillis)
            is CardIntent.ChangeCompletionTime -> _completionPending.update { it?.copy(spentDuration = intent.value.toLongOrNull()?.minutes ?: Duration.ZERO) }
            is CardIntent.MoreCompletionTime -> _completionPending.update { it?.copy(spentDuration = (it.spentDuration + 1.minutes)) }
            is CardIntent.LessCompletionTime -> _completionPending.update { it?.copy(spentDuration = (it.spentDuration - 1.minutes).coerceAtLeast(Duration.ZERO)) }
            is CardIntent.ConfirmCompletion -> confirmCompletion()
            is CardIntent.DismissCompletion -> _completionPending.value = null
        }
    }

    private fun swipeTopCard(direction: SwipeDirection) {
        if (direction != SwipeDirection.LEFT) return
        val card = _sessionCards.value.firstOrNull() ?: return
        viewModelScope.launch { postponeCardUseCase(card.id) }
        _sessionCards.update { it.drop(1) }
    }

    private fun requestCompleteCard(totalMillis: Long) {
        if (_completionPending.value != null) return
        val card = _sessionCards.value.firstOrNull() ?: return
        _completionPending.value = CardCompletionPending(
            cardId = card.id,
            cardName = card.name,
            spentDuration = totalMillis.milliseconds.inWholeMinutes.minutes,
        )
    }

    private fun confirmCompletion() {
        val pending = _completionPending.value ?: return
        _completionPending.value = null
        viewModelScope.launch { completeCardUseCase(pending.cardId, pending.spentDuration.inWholeMilliseconds) }
        _sessionCards.update { it.drop(1) }
    }
}
