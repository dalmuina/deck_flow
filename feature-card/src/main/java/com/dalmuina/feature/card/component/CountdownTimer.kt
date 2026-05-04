package com.dalmuina.feature.card.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dalmuina.core.design_system.component.button.DFButtonIconPrimary
import com.dalmuina.core.design_system.component.button.DFButtonIconSecondary
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.presentation.timer.TimerState
import com.dalmuina.feature.card.presentation.timer.toTimerText

@Composable
fun CountdownTimer(
    state: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {

    val displayMillis = when {
        state.isOvertime -> state.elapsedMillis - state.totalMillis
        state.isRunning || state.remainingMillis < state.totalMillis -> state.remainingMillis
        else -> state.totalMillis
    }

    val isPlaying = state.isRunning

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = displayMillis.toTimerText(),
            style = MaterialTheme.typography.displayLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DFButtonIconPrimary(
                icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "",
            ) {
                if (isPlaying) onPlay(true)
                else onPlay(false)
            }

            Spacer(
                modifier = Modifier
                    .width(Spacing.l)
            )
            DFButtonIconSecondary (
                icon = Icons.Default.Replay,
                contentDescription = "",
            ) {
                onPlay(false)
                onReset()
            }
        }
    }
}

@DFPreview
@Composable
fun CountdownTimerPreview() {
    DeckFlowTheme {
        CountdownTimer(
            state = TimerState(
                totalMillis = 15000L,
                remainingMillis = 0L,
                isRunning = false,
            ),
            onPlay = {},
            onReset = {},
        )
    }
}
