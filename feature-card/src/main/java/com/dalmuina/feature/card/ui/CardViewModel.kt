package com.dalmuina.feature.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.CompleteCardUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.domain.usecase.PostponeCardUseCase
import com.dalmuina.feature.card.model.DFCardUi
import com.dalmuina.feature.card.model.SwipeDirection
import com.dalmuina.feature.card.model.toCardUi
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

class CardViewModel(
    private val getSelectedDeckUseCase: GetSelectedDeckUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val completeCardUseCase: CompleteCardUseCase,
    private val postponeCardUseCase: PostponeCardUseCase,
) : ViewModel() {

    private val sessionCards = MutableStateFlow<List<DFCardUi>>(emptyList())
    private var currentDeckId: Int? = null


    @OptIn(ExperimentalCoroutinesApi::class)
    val deckFlow =
        getSelectedDeckUseCase()
            .flatMapLatest { deckId ->
                if (deckId == null) {
                    flowOf(null)
                } else {
                    getDeckByIdUseCase(deckId)
                }
            }

    init {
        viewModelScope.launch {
            deckFlow.collectLatest { result ->

                if (result is DFResult.Success) {
                    val deckId = result.data.id
                    val dbCards = result.data.cards.map { it.toCardUi() }

                    val hasStructureChanged =
                        sessionCards.value.map { it.id } != dbCards.map { it.id }

                    if (currentDeckId != deckId || hasStructureChanged) {
                        currentDeckId = deckId
                        sessionCards.value = dbCards
                        return@collectLatest
                    }

                    val dbCardsById = dbCards.associateBy { it.id }

                    sessionCards.update { current ->
                        current.map { card ->
                            dbCardsById[card.id] ?: card
                        }
                    }
                }
            }
        }
    }

    val uiState: StateFlow<SessionState> =
        combine(deckFlow, sessionCards) { result, session ->
            when (result) {
                is DFResult.Success -> {
                    SessionState(
                        loading = false,
                        name = result.data.name,
                        cards = session.filter { !it.isCompleted },
                        isDeckSelected = true,
                    )
                }
                else -> {
                    SessionState(
                        loading = false,
                        isDeckSelected = false,
                    )
                }
            }
        }
            .onStart {
                emit(SessionState(loading = true))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                SessionState(loading = true)
            )

    fun process(intent: CardIntent) {
        when (intent) {
            is CardIntent.SwipeTopCard -> swipeTopCard(intent.direction, intent.total)
        }
    }

    private fun swipeTopCard(direction: SwipeDirection, spentMillis:Long) {
        val card = sessionCards.value.firstOrNull() ?: return

        viewModelScope.launch {
            when (direction) {
                SwipeDirection.RIGHT -> completeCardUseCase(card.id, spentMillis)
                SwipeDirection.LEFT -> postponeCardUseCase(card.id)
            }
        }

        sessionCards.update { cards ->
            cards.drop(1)
        }
    }
}
