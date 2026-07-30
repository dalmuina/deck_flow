package com.dalmuina.feature.deck.presentation.deckCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.core.presentation.events.UiEvent
import com.dalmuina.core.presentation.events.UiEventDispatcher
import com.dalmuina.core.presentation.mappers.toUiText
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onFailure
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.DeleteCardUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.SetDeckCardsUseCase
import com.dalmuina.domain.usecase.UpdateDeckNameUseCase
import com.dalmuina.feature.deck.model.CardUi
import com.dalmuina.feature.deck.model.toUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class DeckCreatorViewModel(
    mode: DeckCreatorMode,
    getAllCardsUseCase: GetAllCardsUseCase,
    private val createDeckUseCase: CreateDeckUseCase,
    private val updateDeckNameUseCase: UpdateDeckNameUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val setDeckCardsUseCase: SetDeckCardsUseCase,
    private val uiEventDispatcher: UiEventDispatcher
) : ViewModel() {

    companion object {
        private const val DEFAULT_DECK_NAME = "Deck name"
        private const val STOP_SUBSCRIPTION = 5_000L
        private const val DELAY_PROCESS_INPUT = 1_000L
    }

    private val selectedCards =
        MutableStateFlow<List<SelectedCard>>(emptyList())
    private val deckName = MutableStateFlow(DEFAULT_DECK_NAME)
    private val cardPendingDelete = MutableStateFlow<CardUi?>(null)
    private val resolvedDeckId = MutableStateFlow(mode.deckId)

    init {
        mode.deckId?.let { deckId ->
            loadDeck(deckId)
        }
        observeDeckNameChange()
    }

    private val cardsUiFlow =
        getAllCardsUseCase()
            .map { result ->

                when (result) {
                    is DFResult.Success -> {
                        result.data.map { it.toUi() }
                    }

                    is DFResult.Error -> emptyList()
                }
            }

    private fun observeDeckNameChange() {
        @OptIn(FlowPreview::class)
        deckName
            .debounce(DELAY_PROCESS_INPUT.milliseconds)
            .distinctUntilChanged()
            .onEach { name ->
                resolvedDeckId.value?.let { deckId ->
                    updateDeckNameUseCase(deckId, name)
                }
            }
            .launchIn(viewModelScope)
    }

    val uiState: StateFlow<DeckCreatorState> =
        combine(
            cardsUiFlow,
            selectedCards,
            deckName,
            cardPendingDelete
        ) { cards, selected, name, pendingDelete ->

            val orderMap = selected.associate { it.id to it.order }

            val deckCards = cards
                .map { card ->
                    val order = orderMap[card.id]

                    card.copy(
                        isSelected = order != null,
                        order = order
                    )
                }
                .sortedWith(
                    compareBy<CardUi> { !it.isSelected }
                        .thenBy { it.order ?: Int.MAX_VALUE }
                )

            DeckCreatorState(
                loading = false,
                deckCard = deckCards,
                name = name,
                cardPendingDelete = pendingDelete
            )
        }
            .onStart {
                emit(DeckCreatorState(loading = true))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_SUBSCRIPTION),
                initialValue = DeckCreatorState(loading = true)
            )

    private fun loadDeck(deckId: Int) {
        getDeckByIdUseCase(deckId)
            .onEach { result ->
                when (result) {
                    is DFResult.Success -> {
                        selectedCards.value =
                            result.data.cards
                                .mapNotNull { card ->
                                    card.order?.let { order ->
                                        SelectedCard(card.id, order)
                                    }
                                }
                                .sortedBy { it.order }
                        deckName.value = result.data.name
                    }

                    is DFResult.Error -> {
                        uiEventDispatcher.dispatch(
                            UiEvent.ShowSnackBar(result.error.toUiText())
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun process(intent: DeckCreatorIntent) {
        when (intent) {
            is DeckCreatorIntent.SelectedCard -> selectedCard(intent.id)
            is DeckCreatorIntent.CardCreated ->
                onCardCreated(intent.id)

            is DeckCreatorIntent.NameChanged -> {
                deckName.value = intent.value
            }

            is DeckCreatorIntent.RequestDeleteCard -> requestDeleteCard(intent.id)
            DeckCreatorIntent.ConfirmDeleteCard -> confirmDeleteCard()
            DeckCreatorIntent.DismissDeleteDialog -> cardPendingDelete.value = null

            is DeckCreatorIntent.Reorder -> reorder(intent.from, intent.to)
        }
    }

    private fun persistCards(cardIds: List<Int>) {
        viewModelScope.launch {
            val deckId = resolvedDeckId.value
            if (deckId == null) {
                createDeckUseCase(deckName.value, cardIds)
                    .onSuccess { newDeckId -> resolvedDeckId.value = newDeckId }
                    .onFailure { error ->
                        uiEventDispatcher.dispatch(
                            UiEvent.ShowSnackBar(error.toUiText())
                        )
                    }
            } else {
                setDeckCardsUseCase(deckId, cardIds)
            }
        }
    }

    private fun onCardCreated(cardId: Int) {
        selectedCards.update { current ->
            val newList = current + SelectedCard(
                id = cardId,
                order = current.size
            )

            persistCards(newList.map { it.id })

            newList
        }
    }

    private fun selectedCard(cardId: Int) {

        selectedCards.update { current ->

            val exists = current.any { it.id == cardId }

            val newList =
                if (exists) {
                    current
                        .filterNot { it.id == cardId }
                        .mapIndexed { index, card ->
                            card.copy(order = index)
                        }
                } else {
                    current + SelectedCard(
                        id = cardId,
                        order = current.size
                    )
                }

            persistCards(newList.map { it.id })

            newList
        }
    }

    private fun requestDeleteCard(id: Int) {
        val card = uiState.value.deckCard.firstOrNull { it.id == id } ?: return
        cardPendingDelete.value = card
    }

    private fun confirmDeleteCard() {
        val id = cardPendingDelete.value?.id ?: return
        cardPendingDelete.value = null
        viewModelScope.launch {
            deleteCardUseCase(id)
                .onSuccess { }
                .onFailure { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiText())
                    )
                }
        }
    }

    private fun reorder(from: Int, to: Int) {

        selectedCards.update { current ->

            if (from == to) return@update current

            val mutable = current.toMutableList()

            val item = mutable.removeAt(from)
            mutable.add(to, item)

            val reordered = mutable.mapIndexed { index, card ->
                card.copy(order = index)
            }

            persistCards(reordered.map { it.id })

            reordered
        }
    }
}
