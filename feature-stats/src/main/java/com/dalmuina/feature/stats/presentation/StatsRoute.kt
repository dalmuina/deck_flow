package com.dalmuina.feature.stats.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.component.infoState.DFCircularLoading
import com.dalmuina.designsystem.component.select.DFDropdownSelector
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.stats.component.DailyStatsBarChart
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatsRoute(
    statsViewModel: StatsViewModel = koinViewModel()
) {
    val state by statsViewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.loading -> {
            DFCircularLoading()
        }

        else -> {
            StatsScreen(
                state = state,
                onDeckSelected = { deckId -> statsViewModel.process(StatsIntent.SelectDeck(deckId)) },
                onCardSelected = { cardId -> statsViewModel.process(StatsIntent.SelectCard(cardId)) },
            )
        }
    }
}

@Composable
fun StatsScreen(
    state: StatsUiState,
    onDeckSelected: (Int) -> Unit,
    onCardSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.l)
    ) {
        DFDropdownSelector(
            label = "Deck",
            options = state.deckOptions,
            selectedId = state.selectedDeckId,
            onSelected = onDeckSelected,
        )

        Spacer(Modifier.height(Spacing.l))

        DFDropdownSelector(
            label = "Card",
            options = state.cardOptions,
            selectedId = state.selectedCardId,
            onSelected = onCardSelected,
        )

        Spacer(Modifier.height(Spacing.xl))

        if (state.dailyStats.isNotEmpty()) {
            DailyStatsBarChart(
                stats = state.dailyStats
            )
        }
    }
}