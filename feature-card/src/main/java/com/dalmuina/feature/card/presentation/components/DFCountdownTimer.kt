package com.dalmuina.feature.card.presentation.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.card.presentation.TimerState

@Composable
fun DFCountdownTimer(
    state: TimerState,
    onPlay: (Boolean) -> Unit,
    onReset: () -> Unit,
) {

    val displayMillis =
        if (state.isRunning || state.remainingMillis > 0)
            state.remainingMillis
        else
            state.totalMillis

    val minutes = (displayMillis / 1000) / 60
    val seconds = (displayMillis / 1000) % 60

    val isPlaying = state.isRunning

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "%02d:%02d".format(minutes, seconds),
            style = MaterialTheme.typography.displayMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                onPlay(false)
                onReset()
            }) {
                Icon(imageVector = Icons.Default.Replay, contentDescription = "")
            }
            Spacer(
                modifier = Modifier
                    .width(Spacing.l)
            )
            IconButton(onClick = {
                if (isPlaying) onPlay(true)
                else onPlay(false)
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = ""
                )
            }
        }
    }
}

@DFPreview
@Composable
fun DFCountdownTimerPreview() {
    DeckFlowTheme {
        DFCountdownTimer(
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
