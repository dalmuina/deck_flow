package com.dalmuina.feature.deck.presentation.deckCreator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.dialog.DFDialogConfirm
import com.dalmuina.core.design_system.component.infoState.DFLoadingCircular
import com.dalmuina.core.design_system.component.infoState.EmptyState
import com.dalmuina.core.design_system.component.input.DFTextFieldOutlined
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.component.CardSlot
import com.dalmuina.feature.deck.component.SwipeToDelete
import com.dalmuina.feature.deck.model.CardUi
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DeckCreatorRoute(
    mode: DeckCreatorMode,
    viewModel: DeckCreatorViewModel = koinViewModel(parameters = { parametersOf(mode) }),
    createdCardId: Int?,
    onCreatedCardConsumed: () -> Unit,
    onEditCard: (Int) -> Unit,
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(createdCardId) {
        createdCardId?.let { cardId ->
            viewModel.process(DeckCreatorIntent.CardCreated(cardId))
            onCreatedCardConsumed()
        }
    }
    AnimatedContent(targetState = state.loading) { loading ->
        if (loading) {
            DFLoadingCircular()
        } else {
            DeckCreatorScreen(
                items = state.deckCard,
                name = state.name,
                onSelectedCard = { id -> viewModel.process(DeckCreatorIntent.SelectedCard(id)) },
                onNameChanged = { value -> viewModel.process(DeckCreatorIntent.NameChanged(value)) },
                onEditCard = onEditCard,
                onDelete = { id -> viewModel.process(DeckCreatorIntent.RequestDeleteCard(id)) },
                onReorder = { from, to -> viewModel.process(DeckCreatorIntent.Reorder(from, to)) }
            )
        }
    }

    state.cardPendingDelete?.let { card ->
        DFDialogConfirm(
            title = stringResource(R.string.delete_card_dialog_title),
            message = stringResource(R.string.delete_dialog_message, card.name),
            onConfirm = { viewModel.process(DeckCreatorIntent.ConfirmDeleteCard) },
            onDismiss = { viewModel.process(DeckCreatorIntent.DismissDeleteDialog) }
        )
    }
}

@Composable
fun DeckCreatorScreen(
    items: List<CardUi>,
    name: String,
    onSelectedCard: (Int) -> Unit,
    onNameChanged: (String) -> Unit,
    onEditCard: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.l)
        ) {
            DFTextFieldOutlined(
                name = name,
                label = { Text(text = stringResource(R.string.deck_input_name_label)) },
                onNameChanged = onNameChanged
            )
            Spacer(modifier = Modifier.height(Spacing.l))
            val isEmpty = items.isEmpty()
            if (isEmpty) {
                EmptyState(text = stringResource(R.string.no_card_created))
            } else {
                val selectedCount = items.count { it.isSelected }
                val lazyListState = rememberLazyListState()
                val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    val clampedTo = to.index.coerceIn(0, selectedCount - 1)
                    onReorder(from.index, clampedTo)
                }

                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(Spacing.m),
                    contentPadding = PaddingValues(
                        top = Spacing.l,
                        bottom = Spacing.xxl
                    )
                ) {
                    items(
                        items = items,
                        key = { it.id }
                    ) { card ->
                        ReorderableItem(reorderState, key = card.id) {
                            Box(
                                modifier = Modifier.animateItem(
                                    placementSpec = tween(350)
                                )
                            ) {
                                SwipeToDelete(
                                    id = card.id,
                                    onDelete = onDelete
                                ) {
                                    CardSlot(
                                        card = card,
                                        dragHandleModifier = if (card.isSelected) Modifier.draggableHandle() else Modifier,
                                        onEditCard = onEditCard,
                                        onCheckedChanged = { id -> onSelectedCard(id) }
                                    )
                                }
                            }
                        }
                    }
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
                CardUi(
                    id = 0,
                    name = "Test",
                    duration = 0L.hours + 15L.minutes + 0L.seconds,
                    true,
                ),
                CardUi(
                    id = 1,
                    name = "Test",
                    duration = 2L.hours + 20L.minutes + 0L.seconds,
                    false,
                ),
            ),
            name = "name",
            onSelectedCard = {},
            onNameChanged = {},
            onEditCard = {},
            onDelete = {},
            onReorder = { _, _ -> },
        )
    }
}
