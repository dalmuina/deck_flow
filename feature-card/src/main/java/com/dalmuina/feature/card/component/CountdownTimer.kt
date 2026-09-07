package com.dalmuina.feature.card.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.component.button.DFButtonIconPrimary
import com.dalmuina.core.design_system.component.button.DFButtonIconSecondary
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.IconSize
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.presentation.timer.TimerState
import com.dalmuina.feature.card.presentation.timer.toTimerText

@Composable
fun CountdownTimer(
    state: TimerState,
    onGradient: Boolean = false,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {
    val displayMillis =
        when {
            state.isOvertime -> state.elapsedMillis - state.totalMillis
            state.isRunning || state.remainingMillis < state.totalMillis -> state.remainingMillis
            else -> state.totalMillis
        }

    val isPlaying = state.isRunning
    val timerTextColor = if (onGradient) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = displayMillis.toTimerText(),
            style = MaterialTheme.typography.displayLarge,
            color = timerTextColor,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onGradient) {
                FrostedTimerButton(
                    size = 64.dp,
                    icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "",
                    onClick = { if (isPlaying) onPlay(true) else onPlay(false) },
                )
                Spacer(modifier = Modifier.width(Spacing.l))
                FrostedTimerButton(
                    size = 48.dp,
                    icon = Icons.Default.Replay,
                    contentDescription = "",
                    onClick = {
                        onPlay(false)
                        onReset()
                    },
                )
            } else {
                DFButtonIconPrimary(
                    icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "",
                ) {
                    if (isPlaying) {
                        onPlay(true)
                    } else {
                        onPlay(false)
                    }
                }
                Spacer(modifier = Modifier.width(Spacing.l))
                DFButtonIconSecondary(
                    icon = Icons.Default.Replay,
                    contentDescription = "",
                ) {
                    onPlay(false)
                    onReset()
                }
            }
        }
    }
}

@Composable
private fun FrostedTimerButton(
    size: Dp,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                    onClick = onClick,
                ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(IconSize.m),
        )
    }
}

@DFPreview
@Composable
fun CountdownTimerPreview() {
    DeckFlowTheme {
        CountdownTimer(
            state =
                TimerState(
                    totalMillis = 15000L,
                    remainingMillis = 0L,
                    isRunning = false,
                ),
            onPlay = {},
            onReset = {},
        )
    }
}
