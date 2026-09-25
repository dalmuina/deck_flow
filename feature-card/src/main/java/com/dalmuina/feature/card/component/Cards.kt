package com.dalmuina.feature.card.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Elevation
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.model.CardUi
import com.dalmuina.feature.card.presentation.timer.TimerState
import kotlin.time.Duration

@Composable
fun CardWithoutTimer(
    modifier: Modifier = Modifier,
    card: CardUi,
    brush: Brush? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    CardContainer(modifier = modifier, brush = brush, containerColor = containerColor) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CardContent(card = card, onGradient = brush != null)
        }
    }
}

@Composable
fun CardWithTimer(
    modifier: Modifier = Modifier,
    card: CardUi,
    brush: Brush? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    streakDays: Int = 0,
    timerState: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {
    CardContainer(modifier = modifier, brush = brush, containerColor = containerColor) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(Spacing.l),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DFStreakBadge(streakDays = streakDays)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CardContent(card = card, onGradient = brush != null)
                Spacer(modifier = Modifier.height(Spacing.m))
                CountdownTimer(
                    state = timerState,
                    onGradient = brush != null,
                    onPlay = onPlay,
                    onReset = onReset,
                )
            }
        }
    }
}

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    brush: Brush? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(Corner.s)
    Box(
        modifier =
            modifier
                .shadow(elevation = Elevation.m, shape = shape)
                .clip(shape)
                .then(
                    if (brush != null) {
                        Modifier.background(brush)
                    } else {
                        Modifier.background(containerColor)
                    },
                ),
        content = content,
    )
}

@Composable
fun CardContent(
    card: CardUi,
    onGradient: Boolean = false,
) {
    val contentColor = if (onGradient) Color.White else MaterialTheme.colorScheme.onSurface
    val labelColor = if (onGradient) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "ACTIVIDAD",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = labelColor,
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = card.name,
            style = MaterialTheme.typography.headlineMedium,
            color = contentColor,
        )
    }
}

@DFPreview
@Composable
fun CardWithoutTimerPreview() {
    DeckFlowTheme {
        CardWithoutTimer(
            card =
                CardUi(
                    id = 0,
                    name = "Read",
                    duration = Duration.ZERO,
                    isCompleted = false,
                    isPostponed = false,
                ),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@DFPreview
@Composable
fun CardWithTimerGradientPreview() {
    DeckFlowTheme {
        val primary = MaterialTheme.colorScheme.primary
        val secondary = MaterialTheme.colorScheme.secondary
        CardWithTimer(
            card =
                CardUi(
                    id = 0,
                    name = "Read",
                    duration = Duration.ZERO,
                    isCompleted = false,
                    isPostponed = false,
                ),
            brush = Brush.verticalGradient(listOf(primary, secondary)),
            streakDays = 7,
            timerState =
                TimerState(
                    totalMillis = 45 * 60 * 1000L,
                    remainingMillis = 45 * 60 * 1000L,
                    isRunning = false,
                ),
            onPlay = {},
            onReset = {},
        )
    }
}
