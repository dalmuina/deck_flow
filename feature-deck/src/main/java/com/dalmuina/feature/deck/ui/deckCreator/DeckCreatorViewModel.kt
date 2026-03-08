package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.UiEvent
import com.dalmuina.UiEventDispatcher
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetCardsIdsForDeckUseCase
import com.dalmuina.domain.usecase.UpdateDeckUseCase
import com.dalmuina.feature.deck.ui.cardCreator.CardCreatorEvent
import com.dalmuina.feature.deck.ui.model.toCardUi
import com.dalmuina.feature.deck.ui.model.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val updateDeckUseCase: UpdateDeckUseCase,
    private val getCardsIdsForDeckUseCase: GetCardsIdsForDeckUseCase,
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
        }
    }


    private fun saveDeck() {
        viewModelScope.launch {

            val result = when (mode) {

                DeckCreatorMode.Create ->
                    createDeckUseCase(deckName.value, selectedCardIds.value)

                is DeckCreatorMode.Edit ->
                    updateDeckUseCase(mode.deckId, deckName.value, selectedCardIds.value)
            }

            result
                .onSuccess { _events.send(CardCreatorEvent.CloseScreen) }
                .onError { error ->
                    uiEventDispatcher.dispatch(
                        UiEvent.ShowSnackBar(error.toUiMessage())
                    )
                }
        }
    }


    private fun selectedCard(id: Int) {
        selectedCardIds.update { current ->
            if (id in current) current - id
            else current + id
        }
    }
}
