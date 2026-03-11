package com.dalmuina.feature.card.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.GetSelectedDeckUseCase
import com.dalmuina.feature.card.model.DFCardUi
import com.dalmuina.feature.card.model.toCardUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CardViewModel(
    private val getSelectedDeckUseCase: GetSelectedDeckUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
) : ViewModel() {

    private val sessionCards = MutableStateFlow<List<DFCardUi>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val deckFlow =
        getSelectedDeckUseCase()
            .flatMapLatest { deckId ->

                if (deckId == null) {
                    sessionCards.value = emptyList()
                    flowOf(null)
                } else {
                    getDeckByIdUseCase(deckId)
                        .onEach { result ->
                            if (result is DFResult.Success) {
                                sessionCards.value = result.data.cards.map { it.toCardUi() }
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
                        cards = session
                    )
                }

                else -> {
                    SessionState(
                        loading = false,
                        cards = emptyList()
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

    fun completeTopCard() {
        sessionCards.update { cards ->
            if (cards.isEmpty()) cards
            else cards.drop(1) + cards.first()
        }
    }

}
