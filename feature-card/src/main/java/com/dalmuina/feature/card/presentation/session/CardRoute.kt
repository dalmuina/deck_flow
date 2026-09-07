package com.dalmuina.feature.card.presentation.session

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.badge.DFBadgeSwipe
import com.dalmuina.core.design_system.component.infoState.DFLoadingCircular
import com.dalmuina.core.design_system.component.infoState.EmptyState
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.theme.PostponeContainer
import com.dalmuina.core.design_system.theme.SecondaryContainerDark
import com.dalmuina.core.design_system.theme.SuccessContainer
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Dimen
import com.dalmuina.core.design_system.tokens.IconSize
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.R
import com.dalmuina.feature.card.component.CardWithTimer
import com.dalmuina.feature.card.component.CardWithoutTimer
import com.dalmuina.feature.card.component.SwipeCard
import com.dalmuina.feature.card.model.CardUi
import com.dalmuina.feature.card.model.SwipeDirection
import com.dalmuina.feature.card.presentation.timer.TimerForegroundService
import com.dalmuina.feature.card.presentation.timer.TimerIntent
import com.dalmuina.feature.card.presentation.timer.TimerState
import com.dalmuina.feature.card.presentation.timer.TimerViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardRoute(
    cardViewModel: CardViewModel = koinViewModel(),
    timerViewModel: TimerViewModel = koinViewModel(),
) {
    val state by cardViewModel.uiState.collectAsStateWithLifecycle()
    val timerState by timerViewModel.timerState.collectAsStateWithLifecycle()
    val isTimerLoaded by timerViewModel.isLoaded.collectAsStateWithLifecycle()
    val currentCardId = state.cards.firstOrNull()?.id
    val duration =
        state.cards
            .firstOrNull()
            ?.duration
            ?.inWholeMilliseconds ?: 0L

    val context = LocalContext.current
    val activity = context as ComponentActivity
    val currentTimerState by rememberUpdatedState(timerState)
    val currentCardName by rememberUpdatedState(state.cards.firstOrNull()?.name ?: "")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { /* la notificación es informativa; si se deniega, el servicio sigue activo */ }
        LaunchedEffect(Unit) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    DisposableEffect(Unit) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_STOP -> {
                        if (currentTimerState.isRunning) {
                            val endTimeMillis =
                                System.currentTimeMillis() + currentTimerState.remainingMillis
                            ContextCompat.startForegroundService(
                                context,
                                TimerForegroundService.startIntent(
                                    context,
                                    endTimeMillis,
                                    currentCardName,
                                ),
                            )
                        }
                    }

                    Lifecycle.Event.ON_START -> {
                        context.stopService(TimerForegroundService.stopIntent(context))
                        timerViewModel.process(TimerIntent.Sync)
                    }

                    else -> Unit
                }
            }
        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }

    val prevCardId = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(currentCardId, isTimerLoaded) {
        if (!isTimerLoaded || duration <= 0L || currentCardId == null) return@LaunchedEffect
        val shouldReset =
            if (prevCardId.value == null) {
                timerState.totalMillis != duration
            } else {
                prevCardId.value != currentCardId
            }
        if (shouldReset) timerViewModel.process(TimerIntent.Reset(duration, currentCardId))
        prevCardId.value = currentCardId
    }

    when {
        state.loading -> {
            DFLoadingCircular()
        }

        !state.isDeckSelected -> {
            EmptyState(text = stringResource(R.string.no_deck_created))
        }

        state.cards.isEmpty() -> {
            EmptyState(text = stringResource(R.string.no_pending_tasks))
        }

        else -> {
            CardScreen(
                cards = state.cards,
                timerState = timerState,
                onSwiped = { direction ->
                    when (direction) {
                        SwipeDirection.RIGHT -> {
                            val elapsed = if (timerState.elapsedMillis > 0L) timerState.elapsedMillis else duration
                            cardViewModel.process(CardIntent.RequestCompleteCard(elapsed))
                        }
                        SwipeDirection.LEFT -> cardViewModel.process(CardIntent.SwipeTopCard(direction))
                    }
                },
                onPlay = { isPLaying ->
                    if (isPLaying) {
                        timerViewModel.process(TimerIntent.Pause)
                    } else {
                        timerViewModel.process(TimerIntent.Resume)
                    }
                },
                onReset = {
                    currentCardId?.let { timerViewModel.process(TimerIntent.Reset(duration, it)) }
                },
            )
        }
    }

    state.completionPending?.let { pending ->
        CardCompletionDialog(
            pending = pending,
            onTimeChanged = { cardViewModel.process(CardIntent.ChangeCompletionTime(it)) },
            onMoreTime = { cardViewModel.process(CardIntent.MoreCompletionTime) },
            onLessTime = { cardViewModel.process(CardIntent.LessCompletionTime) },
            onDismiss = { cardViewModel.process(CardIntent.DismissCompletion) },
            onConfirm = { cardViewModel.process(CardIntent.ConfirmCompletion) },
        )
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
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    fun brushForDepth(depth: Int): Brush =
        when (depth % 2) {
            0 -> Brush.verticalGradient(listOf(primary, secondary))
            else -> Brush.verticalGradient(listOf(secondary, primary))
        }

    Box(
        modifier =
            Modifier
                .padding(Spacing.l)
                .fillMaxSize(),
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
                targetValue = targetScale,
                label = "cardScale",
            )

            val baseOffset = (depth * 16).dp

            val targetOffset =
                if (depth == 1) {
                    baseOffset * (1 - swipeProgress)
                } else {
                    baseOffset
                }

            val offset by animateDpAsState(
                targetValue = targetOffset,
                label = "cardOffset",
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Spacer(modifier = Modifier.height(Dimen.m))
                if (depth == 0) {
                    SwipeCard(
                        modifier =
                            Modifier
                                .weight(1f)
                                .offset { IntOffset(x = 0, y = offset.roundToPx()) }
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
                            brush = brushForDepth(depth),
                            timerState = timerState,
                            onPlay = onPlay,
                            onReset = onReset,
                        )
                    }
                } else {
                    CardWithoutTimer(
                        modifier = Modifier.weight(1f),
                        card = card,
                        brush = brushForDepth(depth),
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
    brush: Brush,
    timerState: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {
    val leftAlphaAnimated = (-signedProgress).coerceIn(0f, 1f)
    val rightAlphaAnimated = (signedProgress).coerceIn(0f, 1f)
    Box(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        CardWithTimer(
            card = card,
            brush = brush,
            timerState = timerState,
            onPlay = onPlay,
            onReset = onReset,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(all = Spacing.l),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DFBadgeSwipe(
                    text = stringResource(R.string.completed_level).uppercase(),
                    strokeColor = SuccessContainer,
                    rotation = -5f,
                    alpha = rightAlphaAnimated,
                )
                DFBadgeSwipe(
                    text = stringResource(R.string.postponed_level).uppercase(),
                    strokeColor = PostponeContainer,
                    rotation = 5f,
                    alpha = leftAlphaAnimated,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SwipeDirectionHint(modifier = Modifier.padding(all = Spacing.l))
        }
    }
}

@Composable
private fun SwipeDirectionHint(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SwipeDirectionBadge(
            icons = listOf(Icons.AutoMirrored.Filled.ArrowBack, Icons.Default.Schedule),
            tint = PostponeContainer,
            contentDescription = stringResource(R.string.postponed_level),
        )
        SwipeDirectionBadge(
            icons = listOf(Icons.Default.Check, Icons.AutoMirrored.Filled.ArrowForward),
            tint = SuccessContainer,
            contentDescription = stringResource(R.string.completed_level),
        )
    }
}

@Composable
private fun SwipeDirectionBadge(
    icons: List<ImageVector>,
    tint: Color,
    contentDescription: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .clip(RoundedCornerShape(Corner.m))
                .background(SecondaryContainerDark)
                .padding(horizontal = Spacing.m, vertical = Spacing.s),
    ) {
        icons.forEachIndexed { index, icon ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(Spacing.xs))
            }
            Icon(
                imageVector = icon,
                contentDescription = if (index == 0) contentDescription else null,
                tint = tint,
                modifier = Modifier.size(IconSize.m),
            )
        }
    }
}

@DFPreview
@Composable
fun CardScreenPreview() {
    DeckFlowTheme {
        CardScreen(
            cards =
                listOf(
                    CardUi(
                        id = 0,
                        name = "Reading",
                        duration = 3L.minutes,
                        isCompleted = true,
                        isPostponed = false,
                    ),
                    CardUi(
                        id = 1,
                        name = "Writing",
                        duration = 30L.minutes,
                        isCompleted = false,
                        isPostponed = false,
                    ),
                    CardUi(
                        id = 2,
                        name = "Studying",
                        duration = 1L.hours,
                        isCompleted = false,
                        isPostponed = true,
                    ),
                ),
            timerState =
                TimerState(
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
