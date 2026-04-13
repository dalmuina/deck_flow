package com.dalmuina.feature.deck.presentation.deckSelector

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.dialog.DFConfirmDialog
import com.dalmuina.core.design_system.component.infoState.DFCircularLoading
import com.dalmuina.core.design_system.component.infoState.EmptyState
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.model.DeckUi
import com.dalmuina.feature.deck.presentation.component.DeckSlot
import com.dalmuina.feature.deck.presentation.component.SwipeToDelete
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeckSelectorRoute(
    viewModel: DeckSelectorViewModel = koinViewModel(),
    onEditDeck: (Int) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(targetState = state.loading) { loading ->

        if (loading) {
            DFCircularLoading()
        } else {
            DeckSelectorScreen(
                items = state.deckList,
                onEditDeck = onEditDeck,
                onDelete = { id -> viewModel.process(DeckSelectorIntent.RequestDeleteDeck(id)) },
                onDeckSelected = { id -> viewModel.process(DeckSelectorIntent.SelectDeck(id)) },
            )
        }
    }

    state.deckPendingDelete?.let { deck ->
        DFConfirmDialog(
            title = stringResource(R.string.delete_deck_dialog_title),
            message = stringResource(R.string.delete_dialog_message, deck.name),
            onConfirm = { viewModel.process(DeckSelectorIntent.ConfirmDeleteDeck) },
            onDismiss = { viewModel.process(DeckSelectorIntent.DismissDeleteDialog) }
        )
    }
}

@Composable
fun DeckSelectorScreen(
    items: List<DeckUi>,
    onEditDeck: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onDeckSelected: (Int) -> Unit,
) {
    val isEmpty = items.isEmpty()
    if (isEmpty) {
        EmptyState(stringResource(R.string.no_deck_created))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.l),
            contentPadding = PaddingValues(
                top = Spacing.l,
                bottom = Spacing.xxl
            )
        ) {
            items(
                items,
                key = { it.id }) { deck ->
                Box(
                    modifier = Modifier.animateItem(
                        placementSpec = tween(350)
                    )
                ) {
                    SwipeToDelete(
                        id = deck.id,
                        onDelete = onDelete
                    ) {
                        DeckSlot(
                            deck,
                            onEdit = {
                                onEditDeck(deck.id)
                            },
                            onDeckSelected = { id ->
                                onDeckSelected(id)
                            },
                        )
                    }
                }

            }
        }
    }
}

@DFPreview
@Composable
fun DeckSelectorPreview() {
    DeckFlowTheme {
        DeckSelectorScreen(
            items = listOf(
                DeckUi(
                    id = 0,
                    name = "asd",
                    cardCount = 4,
                    isSelected = true,
                )
            ),
            onEditDeck = {},
            onDelete = {},
            onDeckSelected = {},
        )
    }
}

@DFPreview
@Composable
fun DeckSelectorEmptyPreview() {
    DeckFlowTheme {
        DeckSelectorScreen(
            onEditDeck = {},
            onDelete = {},
            items = emptyList(),
            onDeckSelected = {},
        )
    }
}