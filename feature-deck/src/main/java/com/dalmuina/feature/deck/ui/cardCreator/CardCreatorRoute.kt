package com.dalmuina.feature.deck.ui.cardCreator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.component.button.DFButton
import com.dalmuina.designsystem.component.infoState.DFCircularLoading
import com.dalmuina.designsystem.component.textfield.DFOutlinedTextField
import com.dalmuina.designsystem.component.textfield.DFTimeInput
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun CardCreatorRoute(
    mode: CardCreatorMode,
    viewModel: CardCreatorViewModel = koinViewModel(parameters = { parametersOf(mode) }),
    onCardCreated: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CardCreatorEvent.CloseScreen -> {
                    onCardCreated(event.cardId)
                    onBack()
                }
            }
        }
    }

    if (state.loading) {
        DFCircularLoading()
    } else {
        CardCreatorScreen(
            activity = state.name,
            duration = state.duration,
            onNameChanged = { value -> viewModel.process(CardCreatorIntent.NameChanged(value)) },
            onTimeChanged = { value -> viewModel.process(CardCreatorIntent.TimeChanged(value)) },
            onMoreTime = { viewModel.process(CardCreatorIntent.MoreTime) },
            onLessTime = { viewModel.process(CardCreatorIntent.LessTime) },
            onSaved = { viewModel.process(CardCreatorIntent.SaveActivity) },
            onCancel = onBack,
        )
    }
}

@Composable
fun CardCreatorScreen(
    activity: String,
    duration: Duration,
    onNameChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.l),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        DFOutlinedTextField(
            name = activity,
            label = { Text(text = stringResource(R.string.label_card)) },
            onNameChanged = onNameChanged
        )
        DFTimeInput(
            value = duration,
            onValueChanged = onTimeChanged,
            onMoreTime = onMoreTime,
            onLessTime = onLessTime,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            DFButton(
                text = { Text(text = stringResource(R.string.button_cancel)) },
                onClick = onCancel
            )
            Spacer(
                modifier = Modifier
                    .width(Spacing.l)
            )
            DFButton(
                text = { Text(text = stringResource(R.string.button_ok)) },
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
        CardCreatorScreen(
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
        CardCreatorScreen(
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
        CardCreatorScreen(
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
