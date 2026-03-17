package com.dalmuina.feature.card.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.dalmuina.designsystem.component.button.DFButton
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.card.R
import com.dalmuina.feature.card.ui.TimerState

@Composable
fun DFCountdownTimer(
    state: TimerState,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {

    val displayMillis =
        if (state.isRunning || state.remainingMillis > 0)
            state.remainingMillis
        else
            state.totalMillis

    val minutes = (displayMillis / 1000) / 60
    val seconds = (displayMillis / 1000) % 60
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
            DFButton(
                text = { Text(text = stringResource(R.string.button_stop)) },
                onClick = onStop
            )
            Spacer(
                modifier = Modifier
                    .width(Spacing.l)
            )
            DFButton(
                text = { Text(text = stringResource(R.string.button_start)) },
                onClick = onStart
            )
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
            onStart = {},
            onStop = {},
        )
    }
}
