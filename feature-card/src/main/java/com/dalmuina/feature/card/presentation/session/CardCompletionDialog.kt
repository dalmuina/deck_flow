package com.dalmuina.feature.card.presentation.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.dalmuina.core.design_system.component.button.DFButton
import com.dalmuina.core.design_system.component.input.DFInputTimer
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.card.R
import com.dalmuina.feature.card.model.CardCompletionPending
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardCompletionDialog(
    pending: CardCompletionPending,
    onTimeChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        CardCompletionDialogContent(
            pending = pending,
            onTimeChanged = onTimeChanged,
            onMoreTime = onMoreTime,
            onLessTime = onLessTime,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        )
    }
}

@Composable
fun CardCompletionDialogContent(
    pending: CardCompletionPending,
    onTimeChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Corner.m),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.l),
        ) {
            Text(
                text = stringResource(R.string.completion_dialog_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.padding(top = Spacing.m))
            Text(
                text = pending.cardName,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.padding(top = Spacing.l))
            DFInputTimer(
                value = pending.spentDuration,
                onValueChanged = onTimeChanged,
                onMoreTime = onMoreTime,
                onLessTime = onLessTime,
            )
            Spacer(modifier = Modifier.padding(top = Spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                DFButton(
                    text = { Text(text = stringResource(R.string.cancel_button)) },
                    onClick = onDismiss,
                )
                Spacer(modifier = Modifier.width(Spacing.l))
                DFButton(
                    text = { Text(text = stringResource(R.string.ok_button)) },
                    isEnable = pending.spentDuration.inWholeMinutes > 0,
                    onClick = onConfirm,
                )
            }
        }
    }
}

@DFPreview
@Composable
fun CardCompletionDialogDefaultPreview() {
    DeckFlowTheme {
        CardCompletionDialogContent(
            pending =
                CardCompletionPending(
                    cardId = 1,
                    cardName = "Morning Run",
                    spentDuration = 30.minutes,
                ),
            onTimeChanged = {},
            onMoreTime = {},
            onLessTime = {},
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@DFPreview
@Composable
fun CardCompletionDialogZeroPreview() {
    DeckFlowTheme {
        CardCompletionDialogContent(
            pending =
                CardCompletionPending(
                    cardId = 2,
                    cardName = "Deep Work Session",
                    spentDuration = 0.minutes,
                ),
            onTimeChanged = {},
            onMoreTime = {},
            onLessTime = {},
            onDismiss = {},
            onConfirm = {},
        )
    }
}
