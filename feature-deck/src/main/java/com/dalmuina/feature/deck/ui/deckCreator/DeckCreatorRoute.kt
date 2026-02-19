package com.dalmuina.feature.deck.ui.deckCreator

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.animation.DFAnimations
import com.dalmuina.designsystem.component.button.DFElevatedButton
import com.dalmuina.designsystem.component.button.DFFloatingButton
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.ui.component.DFCardSlot
import com.dalmuina.feature.deck.ui.model.CardUi
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DeckCreatorRoute(
    viewModel: DeckCreatorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DeckCreatorScreen(
        items = state.deckCard,
        onCreate = { viewModel.process(DeckCreatorIntent.Create) },
        onCardClicked = { id -> viewModel.process(DeckCreatorIntent.CardClicked(id)) },
    )
}

@Composable
fun DeckCreatorScreen(
    items: List<CardUi>,
    onCreate: () -> Unit,
    onCardClicked: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
            contentPadding = PaddingValues(
                start = Spacing.l,
                top = Spacing.l,
                end = Spacing.l,
                bottom = Dimens.fabSpacing
            )
        ) {
            items(items) { card ->
                DFCardSlot(
                    card = card
                ) { id ->
                    onCardClicked(id)
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
        val showingButton = items.any { it.isChecked }
            if (showingButton) {

                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Spacing.l),
                    enter = DFAnimations.ScaleFadeIn,
                    exit = DFAnimations.ScaleFadeOut,
                ) {
                    DFElevatedButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.create_deck),
                        onClick = {}
                    )
                }

            } else {

                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }
        DFFloatingButton(
            modifier = Modifier
                .padding(Spacing.l),
            icon = Icons.Default.Add,
            contentDescription = stringResource(R.string.create_card),
            onClick = onCreate
        )
        }
    }
}

@DFPreview
@Composable
fun DeckCreatorScreenPreview() {
    DeckFlowTheme {
        DeckCreatorScreen(
            items = listOf(
                CardUi(
                    id = "0",
                    title = "Test",
                    duration = 0L.hours + 15L.minutes + 0L.seconds,
                    false,
                ),
                CardUi(
                    id = "1",
                    title = "Test",
                    duration = 2L.hours + 20L.minutes + 0L.seconds,
                    true,
                ),
            ),
            onCreate = {},
            onCardClicked = {},
        )
    }
}