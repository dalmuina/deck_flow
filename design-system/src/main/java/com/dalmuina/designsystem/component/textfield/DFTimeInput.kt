package com.dalmuina.designsystem.component.textfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.dalmuina.core.presentation.helpers.toTimerText
import com.dalmuina.design_system.R
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DFTimeInput(
    modifier: Modifier = Modifier,
    value: Duration,
    onValueChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        val externalText = if (value == Duration.ZERO) "" else value.inWholeMinutes.toString()

        var text by rememberSaveable { mutableStateOf(externalText) }

        LaunchedEffect(externalText) {
            if (text != externalText) {
                text = externalText
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = Spacing.s)
                        .semantics(mergeDescendants = true) {
                            stateDescription = "${value.inWholeMinutes} minutes"
                        },
                    value = text,
                    onValueChange = { raw ->
                        val filtered = raw.filter(Char::isDigit)
                        text = filtered
                        onValueChanged(filtered)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    label = { Text(stringResource(R.string.minutes_time_label)) }
                )

                Column(
                    modifier = Modifier.padding(start = Spacing.s),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = onMoreTime) {
                        Icon(
                            modifier = Modifier.size(Dimens.mediumIcons),
                            imageVector = Icons.Default.ArrowDropUp,
                            contentDescription = "More time"
                        )
                    }

                    IconButton(
                        onClick = onLessTime,
                        enabled = value.inWholeMinutes > 0
                    ) {
                        Icon(
                            modifier = Modifier.size(Dimens.mediumIcons),
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Less time"
                        )
                    }
                }
            }

            Text(
                text = value.toTimerText(),
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@DFPreview
@Composable
fun DFTimeInputPreview() {
    DeckFlowTheme {
        DFTimeInput(
            value = 0L.hours + 3L.minutes + 25L.seconds,
            onValueChanged = {},
            onMoreTime = {},
            onLessTime = {},
        )
    }
}