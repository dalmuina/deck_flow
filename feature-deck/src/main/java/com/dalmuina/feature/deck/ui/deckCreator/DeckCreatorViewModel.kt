package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.designsystem.error.toUiMessage
import com.dalmuina.core.ui.UiEvent
import com.dalmuina.core.ui.UiEventDispatcher
import com.dalmuina.core.utils.toggleElement
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.AddCardToDeckUseCase
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.DeleteCardUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.RemoveCardFromDeckUseCase
import com.dalmuina.domain.usecase.UpdateDeckNameUseCase
import com.dalmuina.feature.deck.ui.model.toCardUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

class DeckCreatorViewModel(
    private val mode: DeckCreatorMode,
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val createDeckUseCase: CreateDeckUseCase,
    private val updateDeckNameUseCase: UpdateDeckNameUseCase,
    private val addCardToDeckUseCase: AddCardToDeckUseCase,
    private val removeCardFromDeckUseCase: RemoveCardFromDeckUseCase,
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val uiEventDispatcher: UiEventDispatcher
) : ViewModel() {

    companion object {
        private const val DEFAULT_DECK_NAME = "Deck name"
        private const val STOP_SUBSCRIPTION = 5_000L
        private const val DELAY_PROCESS_INPUT = 1_000L
    }

    private val _events = MutableSharedFlow<DeckCreatorEvent>()
    val events = _events.asSharedFlow()

    private val selectedCardIds = MutableStateFlow<Set<Int>>(emptySet())
    private val deckName = MutableStateFlow(DEFAULT_DECK_NAME)

    init {
        mode.deckId?.let {
            loadDeck(it)
        }
        observeDeckNameChange()
    }

    private val cardsUiFlow =
        getAllCardsUseCase()
            .map { result ->
                when (result) {
                    is DFResult.Success -> result.data.map { it.toCardUi() }
                    is DFResult.Error -> emptyList()
                }
            }


    private fun observeDeckNameChange() {
        @OptIn(FlowPreview::class)
        deckName
            .debounce(DELAY_PROCESS_INPUT)
            .distinctUntilChanged()
            .onEach { name ->
                if (mode is DeckCreatorMode.Edit) {
                    updateDeckNameUseCase(mode.deckId, name)
                }
            }
            .launchIn(viewModelScope)
    }


    val uiState: StateFlow<DeckCreatorUiState> =
        combine(
            cardsUiFlow,
            selectedCardIds,
            deckName
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
                started = SharingStarted.WhileSubscribed(STOP_SUBSCRIPTION),
                initialValue = DeckCreatorUiState(loading = true)
            )

    private fun loadDeck(deckId: Int) {
        getDeckByIdUseCase(deckId)
            .onEach { result ->
                when (result) {
                    is DFResult.Success -> {
                        selectedCardIds.value = result.data.cards.map { it.id }.toSet()
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
            is DeckCreatorIntent.CardCreated ->
                onCardCreated(intent.id)
            DeckCreatorIntent.SaveDeck -> saveDeck()
            is DeckCreatorIntent.NameChanged -> {
                deckName.value = intent.value
            }

            is DeckCreatorIntent.DeleteCard -> deleteCard(intent.id)
        }
    }

    private fun onCardCreated(cardId: Int) {

        selectedCardIds.update { it + cardId }

        if (mode is DeckCreatorMode.Edit) {
            viewModelScope.launch {
                addCardToDeckUseCase(mode.deckId, cardId)
            }
        }
    }


    private fun saveDeck() {
        viewModelScope.launch {
            createDeckUseCase(deckName.value, selectedCardIds.value)
                .onSuccess { _events.emit(DeckCreatorEvent.CloseScreen) }
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }

    private fun selectedCard(cardId: Int) {

        selectedCardIds.update { current ->

            val wasSelected = cardId in current

            if (mode is DeckCreatorMode.Edit) {
                viewModelScope.launch {
                    if (wasSelected)
                        removeCardFromDeckUseCase(mode.deckId, cardId)
                    else
                        addCardToDeckUseCase(mode.deckId, cardId)
                }
            }

            current.toggleElement(cardId)
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
