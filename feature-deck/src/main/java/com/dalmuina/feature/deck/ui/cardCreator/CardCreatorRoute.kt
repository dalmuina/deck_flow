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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.component.button.DFButton
import com.dalmuina.designsystem.component.textfield.DFOutlinedTextField
import com.dalmuina.designsystem.component.textfield.DFTimeInput
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.tokens.Spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun CardCreatorRoute(
    viewModel: CardCreatorViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CardCreatorScreen(
        title = state.title,
        value = state.minutes,
        onTitleChanged = { value -> viewModel.process(CardCreatorIntent.TitleChanged(value)) },
        onTimeChanged = { value -> viewModel.process(CardCreatorIntent.TimeChanged(value)) },
        onMoreTime = { viewModel.process(CardCreatorIntent.MoreTime) },
        onLessTime = { viewModel.process(CardCreatorIntent.LessTime) },
        onCancel = onBack
    )
}

@Composable
fun CardCreatorScreen(
    title: String,
    value: String,
    onTitleChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onMoreTime: () -> Unit,
    onLessTime: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.l),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        DFOutlinedTextField(
            title = title,
            label = { Text(text = "Activity") },
            onTitleChanged = onTitleChanged
        )
        DFTimeInput(
            value = value,
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
                text = { Text(text = "Cancel") },
                onClick = onCancel
            )
            Spacer(
                modifier = Modifier
                    .width(Spacing.l)
            )
            DFButton(
                text = { Text(text = "Ok") },
                onClick = {}
            )

        }

    }
}

@DFPreview
@Composable
fun CardCreatorScreenPreview() {
    CardCreatorScreen(
        title = "Fitness",
        value = "0",
        onTitleChanged = {},
        onTimeChanged = {},
        onMoreTime = {},
        onLessTime = {},
        onCancel = {}
    )
}