package com.dalmuina.feature.deck.presentation.deckCreator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dalmuina.core.design_system.component.button.DFElevatedButton
import com.dalmuina.core.design_system.component.dialog.DFConfirmDialog
import com.dalmuina.core.design_system.component.infoState.DFCircularLoading
import com.dalmuina.core.design_system.component.infoState.EmptyState
import com.dalmuina.core.design_system.component.textfield.DFOutlinedTextField
import com.dalmuina.core.design_system.preview.DFPreview
import com.dalmuina.core.design_system.theme.DeckFlowTheme
import com.dalmuina.core.design_system.tokens.Dimen
import com.dalmuina.core.design_system.tokens.Spacing
import com.dalmuina.feature.deck.R
import com.dalmuina.feature.deck.presentation.component.CardSlot
import com.dalmuina.feature.deck.presentation.component.SwipeToDelete
import com.dalmuina.feature.deck.model.CardUi
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
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
    onBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DeckCreatorEvent.CloseScreen -> onBack()
            }
        }
    }

    LaunchedEffect(createdCardId) {
        createdCardId?.let {cardId->
            viewModel.process(DeckCreatorIntent.CardCreated(cardId))
            onCreatedCardConsumed()
        }
    }
    AnimatedContent(targetState = state.loading) {loading->
        if (loading) {
            DFCircularLoading()
        } else {
            DeckCreatorScreen(
                items = state.deckCard,
                name = state.name,
                isEditMode = state.isEditMode,
                onSelectedCard = { id -> viewModel.process(DeckCreatorIntent.SelectedCard(id)) },
                onSaveDeck = { viewModel.process(DeckCreatorIntent.SaveDeck) },
                onNameChanged = { value -> viewModel.process(DeckCreatorIntent.NameChanged(value)) },
                onEditCard = onEditCard,
                onDelete = { id -> viewModel.process(DeckCreatorIntent.RequestDeleteCard(id)) }
            )
        }
    }

    state.cardPendingDelete?.let { card ->
        DFConfirmDialog(
            title = stringResource(R.string.delete_card_dialog_title),
            message = stringResource(R.string.delete_dialog_message,card.name),
            onConfirm = { viewModel.process(DeckCreatorIntent.ConfirmDeleteCard) },
            onDismiss = { viewModel.process(DeckCreatorIntent.DismissDeleteDialog) }
        )
    }
}

@Composable
fun DeckCreatorScreen(
    items: List<CardUi>,
    name: String,
    isEditMode: Boolean,
    onSelectedCard: (Int) -> Unit,
    onSaveDeck: () -> Unit,
    onNameChanged: (String) -> Unit,
    onEditCard: (Int) -> Unit,
    onDelete: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(Spacing.l)
        )
        {
            DFOutlinedTextField(
                name = name,
                label = { Text(text = stringResource(R.string.deck_input_name_label)) },
                onNameChanged = onNameChanged
            )
            val isEmpty = items.isEmpty()
            if (isEmpty) {
                EmptyState(stringResource(R.string.no_card_created))
            } else {
                LazyColumn(
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
                                    onEditCard = onEditCard,
                                    onCheckedChanged = { id -> onSelectedCard(id) }
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(all = Spacing.l),
            visible = items.any { it.isSelected && !isEditMode }
        ) {
            DFElevatedButton(
                text = {
                    Text(stringResource(R.string.create_deck))
                }
            ) {
                onSaveDeck()
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
            isEditMode = false,
            onSelectedCard = {
            },
            onSaveDeck = {},
            onNameChanged = {},
            onEditCard = {},
            onDelete = {},
        )
    }
}