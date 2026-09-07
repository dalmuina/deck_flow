package com.dalmuina.feature.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.core.presentation.helpers.getMonthRange
import com.dalmuina.core.presentation.helpers.toHeatmapLevel
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DailyStatsDomain
import com.dalmuina.domain.usecase.GetAllDecksUseCase
import com.dalmuina.domain.usecase.GetCardStatsUseCase
import com.dalmuina.feature.stats.model.MonthHeatmapDayUi
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
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

class StatsViewModel(
    getAllDecksUseCase: GetAllDecksUseCase,
    private val getCardStatsUseCase: GetCardStatsUseCase,
) : ViewModel() {
    private val selectedCardId = MutableStateFlow<Int?>(null)
    private val currentYearMonth = MutableStateFlow(YearMonth.now())

    private val decksResultFlow = getAllDecksUseCase()

    fun process(intent: StatsIntent) {
        when (intent) {
            is StatsIntent.SelectActivity -> selectedCardId.value = intent.cardId
            StatsIntent.PreviousMonth -> currentYearMonth.value = currentYearMonth.value.minusMonths(1)
            StatsIntent.NextMonth -> currentYearMonth.value = currentYearMonth.value.plusMonths(1)
        }
    }

    private val selectionState: StateFlow<SelectionData> =
        combine(
            decksResultFlow,
            selectedCardId,
        ) { result, selectedCardId ->
            when (result) {
                is DFResult.Success -> {
                    val allCards = result.data.flatMap { it.cards }
                    val activityOptions = allCards.map { it.toUi() }

                    val effectiveCardId =
                        activityOptions
                            .firstOrNull { it.id == selectedCardId }
                            ?.id
                            ?: activityOptions.firstOrNull()?.id

                    val cardDurationMillis =
                        allCards
                            .firstOrNull { it.id == effectiveCardId }
                            ?.durationMillis
                            ?: 0L

                    SelectionData(
                        loading = false,
                        activityOptions = activityOptions,
                        selectedCardId = effectiveCardId,
                        selectedCardDurationMillis = cardDurationMillis,
                    )
                }

                is DFResult.Error ->
                    SelectionData(
                        loading = false,
                        selectedCardId = selectedCardId,
                    )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SelectionData(loading = true),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val statsState: StateFlow<StatsData> =
        combine(
            selectionState
                .map { it.selectedCardId to it.selectedCardDurationMillis }
                .distinctUntilChanged(),
            currentYearMonth,
        ) { (cardId, cardDuration), yearMonth ->
            Triple(cardId, cardDuration, yearMonth)
        }.flatMapLatest { (cardId, cardDuration, yearMonth) ->
            if (cardId == null) return@flatMapLatest flowOf(StatsData())

            val (fromDay, toDay) = getMonthRange(yearMonth.year, yearMonth.monthValue)
            val prevMonth = yearMonth.minusMonths(1)
            val (prevFrom, prevTo) = getMonthRange(prevMonth.year, prevMonth.monthValue)
            val hasNextMonth = yearMonth.isBefore(YearMonth.now())

            combine(
                getCardStatsUseCase(cardId, fromDay, toDay),
                getCardStatsUseCase(cardId, prevFrom, prevTo),
            ) { currentResult, prevResult ->
                when (currentResult) {
                    is DFResult.Success ->
                        StatsData(
                            loading = false,
                            year = yearMonth.year,
                            month = yearMonth.monthValue,
                            heatmapDays =
                                buildMonthHeatmapDays(
                                    yearMonth.year,
                                    yearMonth.monthValue,
                                    currentResult.data,
                                    cardDuration,
                                ),
                            hasPreviousData = prevResult is DFResult.Success && prevResult.data.isNotEmpty(),
                            hasNextMonth = hasNextMonth,
                        )

                    is DFResult.Error -> StatsData(loading = false)
                }
            }.onStart { emit(StatsData(loading = true)) }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StatsData(),
        )

    val uiState: StateFlow<StatsState> =
        combine(selectionState, statsState) { selection, stats ->
            StatsState(
                loading = selection.loading,
                activityOptions = selection.activityOptions,
                selectedCardId = selection.selectedCardId,
                statsLoading = stats.loading,
                year = stats.year,
                month = stats.month,
                heatmapDays = stats.heatmapDays,
                hasPreviousData = stats.hasPreviousData,
                hasNextMonth = stats.hasNextMonth,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            StatsState(loading = true),
        )

    private fun buildMonthHeatmapDays(
        year: Int,
        month: Int,
        stats: List<DailyStatsDomain>,
        cardDurationMillis: Long,
    ): List<MonthHeatmapDayUi> {
        val zone = ZoneId.systemDefault()
        val ym = YearMonth.of(year, month)
        val statsMap =
            stats.associateBy { stat ->
                Instant.ofEpochMilli(stat.dayStart).atZone(zone).dayOfMonth
            }
        return (1..ym.lengthOfMonth()).map { day ->
            val stat = statsMap[day]
            val totalSpent = stat?.totalSpentMillis ?: 0L
            MonthHeatmapDayUi(
                dayOfMonth = day,
                dayStart =
                    ym
                        .atDay(day)
                        .atStartOfDay(zone)
                        .toInstant()
                        .toEpochMilli(),
                totalSpentMillis = totalSpent,
                completedCount = stat?.completedCount ?: 0,
                level = totalSpent.toHeatmapLevel(cardDurationMillis),
            )
        }
    }
}
