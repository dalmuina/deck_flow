package com.dalmuina.feature.deck.ui.deckSelector

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.designsystem.preview.DFPreview
import com.dalmuina.designsystem.theme.DeckFlowTheme
import com.dalmuina.designsystem.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.ui.component.DFDeckSlot
import com.dalmuina.feature.deck.ui.model.DeckUi
import com.dalmuina.feature.deck.ui.model.EnergyLevel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeckSelectorRoute(
    viewModel: DeckSelectorViewModel = koinViewModel(),
    onAddDeck: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.process(DeckSelectorIntent.LoadDecks)
    }
    DeckSelectorScreen(
        onAddDeck = onAddDeck,
        items = state.deckList,
    )
}

@Composable
fun DeckSelectorScreen(
    onAddDeck: () -> Unit,
    items: List<DeckUi>,
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
            verticalArrangement = Arrangement.spacedBy(Spacing.l)
        ) {
            items(items, key = { it.id }) { deck ->
                DFDeckSlot(
                    deck,
                    onEdit = {
                        onAddDeck()
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
            onAddDeck = {},
            items = listOf(
                DeckUi(
                    id = 0,
                    cardList = listOf(1, 2, 5),
                    energy = EnergyLevel.HIGH
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
            onAddDeck = {},
            items = emptyList(),
        )
    }
}