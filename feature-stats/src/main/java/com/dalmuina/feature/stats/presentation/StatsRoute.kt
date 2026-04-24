package com.dalmuina.feature.stats.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.infoState.DFLoadingCircular
import com.dalmuina.core.design_system.component.select.DFDropdownSelector
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.stats.component.HeatmapCalendar
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatsRoute(
    statsViewModel: StatsViewModel = koinViewModel(),
) {
    val state by statsViewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.loading -> DFLoadingCircular()
        else -> StatsScreen(
            state = state,
            onActivitySelected = { statsViewModel.process(StatsIntent.SelectActivity(it)) },
            onPreviousMonth = { statsViewModel.process(StatsIntent.PreviousMonth) },
            onNextMonth = { statsViewModel.process(StatsIntent.NextMonth) },
        )
    }
}

@Composable
fun StatsScreen(
    state: StatsState,
    onActivitySelected: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.l),
    ) {
        DFDropdownSelector(
            label = "Actividad",
            options = state.activityOptions,
            selectedId = state.selectedCardId,
            onSelected = onActivitySelected,
        )

        Spacer(Modifier.height(Spacing.xl))

        when {
            state.statsLoading && state.year == 0 -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            }
            state.year > 0 -> {
                HeatmapCalendar(
                    year = state.year,
                    month = state.month,
                    days = state.heatmapDays,
                    hasPreviousData = state.hasPreviousData,
                    hasNextMonth = state.hasNextMonth,
                    isLoading = state.statsLoading,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                )
            }
        }
    }
}
