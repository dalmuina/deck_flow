package com.dalmuina.feature.deck.ui.deckSelector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Dimens
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.ui.component.DFDeckSlot
import com.dalmuina.feature.deck.ui.model.DFDeckUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeckSelectorRoute(
    viewModel: DeckSelectorViewModel = koinViewModel(),
    onEditDeck: (Int) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DeckSelectorScreen(
        onEditDeck = onEditDeck,
        items = state.deckList,
    )
}

@Composable
fun DeckSelectorScreen(
    onEditDeck: (Int) -> Unit,
    items: List<DFDeckUi>,
) {
    val isEmpty = items.isEmpty()
    if (isEmpty) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_deck_created))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.l),
            contentPadding = PaddingValues(
                top= Spacing.l,
                bottom= Dimens.fabSpacing
            )
        ) {
            items(items, key = { it.id }) { deck ->
                DFDeckSlot(
                    deck,
                    onEdit = {
                        onEditDeck(deck.id)
                    },
                    onDelete = {}
                )
            }
        }
    }
}

@DFPreview
@Composable
fun DeckSelectorPreview() {
    DeckFlowTheme {
        DeckSelectorScreen(
            onEditDeck = {},
            items = listOf(
                DFDeckUi(
                    id = 0,
                    name = "asd",
                    cardCount = 4,
                )
            ),
        )
    }
}

@DFPreview
@Composable
fun DeckSelectorEmptyPreview() {
    DeckFlowTheme {
        DeckSelectorScreen(
            onEditDeck = {},
            items = emptyList(),
        )
    }
}