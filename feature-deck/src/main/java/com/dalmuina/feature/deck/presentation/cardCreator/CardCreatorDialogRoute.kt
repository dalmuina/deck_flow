package com.dalmuina.feature.deck.presentation.cardCreator

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.button.DFButton
import com.dalmuina.core.design_system.component.infoState.DFCircularLoading
import com.dalmuina.core.design_system.component.textfield.DFOutlinedTextField
import com.dalmuina.core.design_system.component.textfield.DFTimeInput
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Corner
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.deck.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardCreatorDialogNavRoute(
    mode: CardCreatorMode,
    viewModel: CardCreatorViewModel = koinViewModel(parameters = { parametersOf(mode) }),
    onCardSaved: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var closing by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CardCreatorEvent.CloseScreen -> {
                    if (!closing) {
                        closing = true
                        if (event.cardId != null) onCardSaved(event.cardId)
                        onDismiss()
                    }
                }
            }
        }
    }

    CardCreatorDialog(
        loading = state.loading,
        processing = state.processing,
        activity = state.name,
        duration = state.duration,
        onNameChanged = { value -> viewModel.process(CardCreatorIntent.NameChanged(value)) },
        onTimeChanged = { value -> viewModel.process(CardCreatorIntent.TimeChanged(value)) },
        onMoreTime = { viewModel.process(CardCreatorIntent.MoreTime) },
        onLessTime = { viewModel.process(CardCreatorIntent.LessTime) },
        onSaved = {
            if (!closing) {
                viewModel.process(CardCreatorIntent.SaveActivity)
            }
        },
        onDismiss = {
            if (!closing) {
                viewModel.process(CardCreatorIntent.Cancel)
            }
        },
    )
}

@Composable
fun CardCreatorDialog(
    loading: Boolean,
    processing: Boolean,
    activity: String,
    duration: Duration,
    onNameChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
    onSaved: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Corner.m)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp)
                    .padding(Spacing.l),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = loading) { loading ->
                    if (loading) {
                        DFCircularLoading()
                    } else {
                        CardCreatorContent(
                            processing = processing,
                            activity = activity,
                            duration = duration,
                            onNameChanged = onNameChanged,
                            onTimeChanged = onTimeChanged,
                            onMoreTime = onMoreTime,
                            onLessTime = onLessTime,
                            onSaved = onSaved,
                            onCancel = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardCreatorContent(
    processing: Boolean,
    activity: String,
    duration: Duration,
    onNameChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester =
        remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    Column(
        modifier =
            modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            DFOutlinedTextField(
                name = activity,
                onNameChanged = onNameChanged,
                focusRequester = focusRequester,
                label = { Text(text = stringResource(R.string.label_card)) },
            )
            Spacer(modifier = Modifier.height(Spacing.l))
            DFTimeInput(
                value =
                    duration,
                onValueChanged = onTimeChanged, onMoreTime = onMoreTime, onLessTime = onLessTime,
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xl))
        Row(
            modifier =
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            DFButton(text = {
                Text(
                    text = stringResource(R.string.cancel_button)
                )
            }, onClick = onCancel)
            Spacer(modifier = Modifier.width(Spacing.l))
            DFButton(
                isLoading = processing,
                text = { Text(text = stringResource(R.string.ok_button)) },
                isEnable = activity.isNotEmpty() && duration.inWholeMinutes > 0,
                onClick = onSaved
            )
        }
    }
}

@DFPreview
@Composable
fun CardCreatorNoTimePreview() {
    DeckFlowTheme {
        CardCreatorContent(
            processing = false,
            activity = "Fitness",
            duration = 3L.minutes,
            onNameChanged = {},
            onTimeChanged = {},
            onMoreTime = {},
            onLessTime = {},
            onCancel = {},
            onSaved = {},
        )
    }
}

@DFPreview
@Composable
fun CardCreatorButtonEnabledPreview() {
    DeckFlowTheme {
        CardCreatorContent(
            processing = true,
            activity = "Fitness",
            duration = 24.minutes,
            onNameChanged = {},
            onTimeChanged = {},
            onMoreTime = {},
            onLessTime = {},
            onCancel = {},
            onSaved = {},
        )
    }
}

@DFPreview
@Composable
fun CardCreatorNoTitlePreview() {
    DeckFlowTheme {
        CardCreatorContent(
            processing = false,
            activity = "",
            duration = 10.hours,
            onNameChanged = {},
            onTimeChanged = {},
            onMoreTime = {},
            onLessTime = {},
            onCancel = {},
            onSaved = {},
        )
    }
}
