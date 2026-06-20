package com.dalmuina.core.design_system.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.dalmuina.core.design_system.R
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.IconSize
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.core.presentation.helpers.toTimerText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DFInputTimer(
    modifier: Modifier = Modifier,
    value: Duration,
    onValueChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
) {
    val externalText = if (value == Duration.ZERO) "" else value.inWholeMinutes.toString()

    var text by rememberSaveable { mutableStateOf(externalText) }

    LaunchedEffect(externalText) {
        if (text != externalText) {
            text = externalText
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            label = { Text(stringResource(R.string.minutes_time_label)) },
            trailingIcon = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = onMoreTime,
                        ) {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = Icons.Default.ArrowDropUp,
                            contentDescription = "More time"
                        )
                    }
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        enabled = value.inWholeMinutes > 0,
                        onClick = onLessTime,
                    ) {
                        Icon(
                            modifier = Modifier.size(IconSize.m),
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Less time"
                        )
                    }
                }
            }
        )

        Text(
            text = value.toTimerText(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@DFPreview
@Composable
fun DFInputTimerPreview() {
    DeckFlowTheme {
        DFInputTimer(
            value = 0L.hours + 3L.minutes + 25L.seconds,
            onValueChanged = {},
            onMoreTime = {},
            onLessTime = {},
        )
    }
}