package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.UiEvent
import com.dalmuina.UiEventDispatcher
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.AddCardToDeckUseCase
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.DeleteCardUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetCardsIdsForDeckUseCase
import com.dalmuina.domain.usecase.RemoveCardFromDeckUseCase
import com.dalmuina.domain.usecase.UpdateDeckNameUseCase
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorEvent
import com.dalmuina.feature.deck.ui.model.toCardUi
import com.dalmuina.feature.deck.ui.model.toUiMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckCreatorViewModel(
    private val mode: DeckCreatorMode,
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val createDeckUseCase: CreateDeckUseCase,
    private val updateDeckNameUseCase: UpdateDeckNameUseCase,
    private val addCardToDeckUseCase: AddCardToDeckUseCase,
    private val removeCardFromDeckUseCase: RemoveCardFromDeckUseCase,
    private val getCardsIdsForDeckUseCase: GetCardsIdsForDeckUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val uiEventDispatcher: UiEventDispatcher
) : ViewModel() {

    private val _events = Channel<CardCreatorEvent>()
    val events = _events.receiveAsFlow()

    private val selectedCardIds = MutableStateFlow<Set<Int>>(emptySet())

    init {
        mode.deckId?.let {
            loadDeck(it)
        }
    }

    private val cardsUiFlow =
        getAllCardsUseCase()
            .map { result ->
                when (result) {
                    is DFResult.Success -> result.data.map { it.toCardUi() }
                    is DFResult.Error -> emptyList()
                }
            }

    private val deckName = MutableStateFlow("New Deck")

    init {
        @OptIn(FlowPreview::class)
        deckName
            .debounce(1000)
            .distinctUntilChanged()
            .onEach { name ->
                if (mode is DeckCreatorMode.Edit) {
                    updateDeckNameUseCase(mode.deckId, deckName.value)
                }
            }
            .launchIn(viewModelScope)
    }


    val uiState: StateFlow<DeckCreatorUiState> =
        combine(
            flow = cardsUiFlow,
            flow2 = selectedCardIds,
            flow3 = deckName
        ) { cards, selectedIds, name ->

            DeckCreatorUiState(
                loading = false,
                deckCard = cards.map { card ->
                    card.copy(isSelected = selectedIds.contains(card.id))
                },
                name = name,
                isEditMode = mode is DeckCreatorMode.Edit
            )
        }
            .onStart {
                emit(DeckCreatorUiState(loading = true))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DeckCreatorUiState(loading = true)
            )

    private fun loadDeck(deckId: Int) {

        getCardsIdsForDeckUseCase(deckId)
            .onEach { result ->
                when (result) {
                    is DFResult.Success -> {
                        selectedCardIds.value = result.data.cardIds.toSet()
                        deckName.value = result.data.name
                    }

                    is DFResult.Error -> {
                        uiEventDispatcher.dispatch(
                            UiEvent.ShowSnackBar(result.error.toUiMessage())
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun process(intent: DeckCreatorIntent) {
        when (intent) {
            is DeckCreatorIntent.SelectedCard -> selectedCard(intent.id)
            DeckCreatorIntent.SaveDeck -> saveDeck()
            is DeckCreatorIntent.NameChanged -> {
                deckName.value = intent.value
            }

            is DeckCreatorIntent.DeleteCard -> deleteCard(intent.id)
        }
    }


    private fun saveDeck() {
        viewModelScope.launch {
            createDeckUseCase(deckName.value, selectedCardIds.value)
                .onSuccess { _events.send(CardCreatorEvent.CloseScreen) }
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }

    private fun selectedCard(cardId: Int) {

        val wasSelected = cardId in selectedCardIds.value

        selectedCardIds.update { current ->
            current.toggle(cardId)
        }

        if (mode is DeckCreatorMode.Edit) {
            viewModelScope.launch {
                if (wasSelected) {
                    removeCardFromDeckUseCase(mode.deckId, cardId)
                } else {
                    addCardToDeckUseCase(mode.deckId, cardId)
                }
            }
        }
    }

    private fun deleteCard(id: Int) {
        viewModelScope.launch {
            deleteCardUseCase(id)
                .onSuccess { }
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }
}

fun Set<Int>.toggle(id: Int) =
    if (id in this) this - id else this + id