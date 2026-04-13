package com.dalmuina.feature.card.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.badge.DFSwipeBadge
import com.dalmuina.core.design_system.component.infoState.DFCircularLoading
import com.dalmuina.core.design_system.component.infoState.EmptyState
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.Amber400
import com.dalmuina.core.design_system.theme.Amber700
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.theme.Green500
import com.dalmuina.core.design_system.theme.Green800
import com.dalmuina.core.design_system.tokens.Dimen
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.R
import com.dalmuina.feature.card.model.CardUi
import com.dalmuina.feature.card.model.SwipeDirection
import com.dalmuina.feature.card.presentation.components.CardWithoutTimer
import com.dalmuina.feature.card.presentation.components.CardWithTimer
import com.dalmuina.feature.card.presentation.components.SwipeCard
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardRoute(
    cardViewModel: CardViewModel = koinViewModel(),
    timerViewModel: TimerViewModel = koinViewModel()
) {
    val state by cardViewModel.uiState.collectAsStateWithLifecycle()
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val currentCardId = state.cards.firstOrNull()?.id
    val duration = state.cards.firstOrNull()?.duration?.inWholeMilliseconds ?: 0L

    LaunchedEffect(currentCardId) {
        if (duration > 0L) {
            timerViewModel.process(TimerIntent.Reset(duration))
        }
    }

    when {
        state.loading -> {
            DFCircularLoading()
        }

        !state.isDeckSelected -> {
            EmptyState(stringResource(R.string.no_deck_created))
        }

        state.cards.isEmpty() -> {
            EmptyState(stringResource(R.string.no_pending_tasks))
        }

        else -> {
            CardScreen(
                cards = state.cards,
                timerState = timerState,
                onSwiped = { direction ->
                    cardViewModel.process(
                        CardIntent.SwipeTopCard(
                            direction,
                            (timerState.totalMillis - timerState.remainingMillis)
                        )
                    )
                },
                onPlay = { isPLaying ->
                    if (isPLaying)
                        timerViewModel.process(TimerIntent.Pause)
                    else
                        timerViewModel.process(TimerIntent.Resume)
                },
                onReset = {
                    timerViewModel.process(TimerIntent.Reset(duration))
                },
            )
        }
    }
}

@Composable
fun CardScreen(
    cards: List<CardUi>,
    timerState: TimerState,
    onSwiped: (SwipeDirection) -> Unit,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(Spacing.l)
            .fillMaxSize()
    ) {
        var dragOffsetX by remember { mutableFloatStateOf(0f) }
        val swipeProgress = (abs(dragOffsetX) / 300f).coerceIn(0f, 1f)
        val signedProgress = (dragOffsetX / 300f).coerceIn(-1f, 1f)

        cards.reversed().forEachIndexed { index, card ->

            val depth = cards.size - index - 1
            val baseScale = 1f - (depth * 0.05f)

            val targetScale =
                if (depth == 1) {
                    baseScale + (0.05f * swipeProgress)
                } else {
                    baseScale
                }

            val scale by animateFloatAsState(
                targetValue = targetScale, label = "cardScale"
            )

            val baseOffset = (depth * 16).dp

            val targetOffset =
                if (depth == 1) {
                    baseOffset * (1 - swipeProgress)
                } else {
                    baseOffset
                }

            val offset by animateDpAsState(
                targetValue = targetOffset, label = "cardOffset"
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ){
                Spacer(modifier = Modifier.height(Dimen.m))
                if (depth == 0) {
                    SwipeCard(
                        modifier = Modifier
                            .weight(1f)
                            .offset(y = offset)
                            .scale(scale),
                        onSwiped = { direction ->
                            dragOffsetX = 0f
                            onSwiped(direction)
                        },
                        onDragProgress = { dragOffsetX = it },
                    ) {
                        SwipeCardContent(
                            signedProgress = signedProgress,
                            card = card,
                            timerState = timerState,
                            onPlay = onPlay,
                            onReset = onReset,
                        )
                    }
                } else {
                    CardWithoutTimer(
                        modifier = Modifier.weight(1f),
                        card = card,
                    )
                }
                Spacer(modifier = Modifier.height(Dimen.m))
            }
        }
    }

}

@Composable
fun SwipeCardContent(
    modifier: Modifier = Modifier,
    signedProgress: Float,
    card: CardUi,
    timerState: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {

    val leftAlphaAnimated = (-signedProgress).coerceIn(0f, 1f)
    val rightAlphaAnimated = (signedProgress).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        CardWithTimer(
            card = card,
            containerColor = MaterialTheme.colorScheme.surface,
            timerState = timerState,
            onPlay = onPlay,
            onReset = onReset,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Spacing.l),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            DFSwipeBadge(
                text = stringResource(R.string.completed_level).uppercase(),
                backgroundColor = Amber400,
                strokeColor = Amber700,
                alpha = rightAlphaAnimated
            )
            DFSwipeBadge(
                text = stringResource(R.string.postponed_level).uppercase(),
                backgroundColor = Green500,
                strokeColor = Green800,
                alpha = leftAlphaAnimated
            )
        }

    }
}

@DFPreview
@Composable
fun CardScreenPreview() {

    DeckFlowTheme {
        CardScreen(
            cards = listOf(
                CardUi(
                    id = 0,
                    name = "Reading",
                    duration = 3L.minutes,
                    isCompleted = true,
                    isPostponed = false,
                ), CardUi(
                    id = 1,
                    name = "Writing",
                    duration = 30L.minutes,
                    isCompleted = false,
                    isPostponed = false,
                ), CardUi(
                    id = 2,
                    name = "Studying",
                    duration = 1L.hours,
                    isCompleted = false,
                    isPostponed = true,
                )
            ),
            timerState = TimerState(
                totalMillis = 15000L,
                remainingMillis = 10000L,
                isRunning = false,
            ),
            onSwiped = {},
            onPlay = {},
            onReset = {},
        )
    }
}
