package com.dalmuina.feature.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.core.presentation.helpers.getLast7DaysRange
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.domain.usecase.GetCardStatsUseCase
import com.dalmuina.feature.stats.model.toUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class StatsViewModel(
    getAllDecksUseCase: GetAllDecksUseCase,
    private val getCardStatsUseCase: GetCardStatsUseCase,
) : ViewModel() {

    private val selectedDeckId = MutableStateFlow<Int?>(null)
    private val selectedCardId = MutableStateFlow<Int?>(null)

    private val decksResultFlow = getAllDecksUseCase()

    fun process(intent: StatsIntent) {
        when (intent) {
            is StatsIntent.SelectCard -> onCardSelected(intent.cardId)
            is StatsIntent.SelectDeck -> onDeckSelected(intent.deckId)
        }
    }

    private val selectionState: StateFlow<SelectionData> =
        combine(
            decksResultFlow,
            selectedDeckId,
            selectedCardId
        ) { result, selectedDeckId, selectedCardId ->
            when (result) {
                is DFResult.Success -> {
                    val decks = result.data
                    val deckOptions = decks.map { it.toUi() }

                    val effectiveDeckId = selectedDeckId ?: decks.firstOrNull()?.id
                    val selectedDeck = decks.firstOrNull { it.id == effectiveDeckId }

                    val cardOptions = selectedDeck
                        ?.cards
                        ?.map { it.toUi() }
                        .orEmpty()

                    val effectiveCardId = cardOptions
                        .firstOrNull { it.id == selectedCardId }
                        ?.id
                        ?: cardOptions.firstOrNull()?.id

                    SelectionData(
                        loading = false,
                        deckOptions = deckOptions,
                        selectedDeckId = effectiveDeckId,
                        cardOptions = cardOptions,
                        selectedCardId = effectiveCardId
                    )
                }

                is DFResult.Error -> {
                    SelectionData(
                        loading = false,
                        selectedDeckId = selectedDeckId,
                        selectedCardId = selectedCardId
                    )
                }
            }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                SelectionData(loading = true)
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val statsState: StateFlow<StatsData> =
        selectionState
            .map { it.selectedCardId }
            .distinctUntilChanged()
            .flatMapLatest { cardId ->
                if (cardId == null) {
                    flowOf(StatsData())
                } else {
                    val (fromDay, toDay) = getLast7DaysRange()

                    getCardStatsUseCase(cardId, fromDay, toDay)
                        .map { result ->
                            when (result) {
                                is DFResult.Success -> StatsData(
                                    loading = false,
                                    dailyStats = result.data.map{it.toUi()}
                                )

                                is DFResult.Error -> StatsData(
                                    loading = false,
                                )
                            }
                        }
                        .onStart { emit(StatsData(loading = true)) }
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                StatsData()
            )

    val uiState: StateFlow<StatsState> =
        combine(
            selectionState,
            statsState
        ) { selection, stats ->
            StatsState(
                loading = selection.loading,
                deckOptions = selection.deckOptions,
                selectedDeckId = selection.selectedDeckId,
                cardOptions = selection.cardOptions,
                selectedCardId = selection.selectedCardId,
                dailyStats = stats.dailyStats,
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                StatsState(loading = true)
            )

    fun onDeckSelected(deckId: Int?) {
        selectedDeckId.value = deckId
        selectedCardId.value = null
    }

    fun onCardSelected(cardId: Int?) {
        selectedCardId.value = cardId
    }
}
