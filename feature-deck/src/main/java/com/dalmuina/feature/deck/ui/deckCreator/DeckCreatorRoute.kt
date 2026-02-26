package com.dalmuina.feature.deck.ui.deckCreator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.ui.component.DFCardSlot
import com.dalmuina.feature.deck.ui.model.DFCardUi
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
        onCardClicked = { id -> viewModel.process(DeckCreatorIntent.CardClicked(id)) },
    )
}

@Composable
fun DeckCreatorScreen(
    items: List<DFCardUi>,
    onCardClicked: (Int) -> Unit,
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
    }
}

@DFPreview
@Composable
fun DeckCreatorScreenPreview() {
    DeckFlowTheme {
        DeckCreatorScreen(
            items = listOf(
                DFCardUi(
                    id = 0,
                    title = "Test",
                    duration = 0L.hours + 15L.minutes + 0L.seconds,
                ),
                DFCardUi(
                    id = 1,
                    title = "Test",
                    duration = 2L.hours + 20L.minutes + 0L.seconds,
                ),
            ),
            onCardClicked = {},
        )
    }
}