package com.dalmuina.feature.deck.ui.deckCreator

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.core.ui.UiEvent
import com.dalmuina.core.ui.UiEventDispatcher
import com.dalmuina.designsystem.error.toUiMessage
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.onError
import com.dalmuina.domain.model.onSuccess
import com.dalmuina.domain.usecase.AddCardToDeckUseCase
import com.dalmuina.domain.usecase.CreateDeckUseCase
import com.dalmuina.domain.usecase.DeleteCardUseCase
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.domain.usecase.GetDeckByIdUseCase
import com.dalmuina.domain.usecase.SetDeckCardsUseCase
import com.dalmuina.domain.usecase.UpdateDeckNameUseCase
import com.dalmuina.feature.deck.ui.model.DFCardSlotUi
import com.dalmuina.feature.deck.ui.deckCreator.SelectedCard
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

    private val _events = MutableSharedFlow<DeckCreatorEvent>()
    val events = _events.asSharedFlow()

    private val selectedCards =
        MutableStateFlow<List<SelectedCard>>(emptyList())
    private val deckName = MutableStateFlow(DEFAULT_DECK_NAME)

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
                        result.data.map { it.toCardUi() }
                    }

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
            selectedCards,
            deckName
        ) { cards, selectedIds, name ->

            val orderMap = selectedCards.value.associate { it.id to it.order }

            DeckCreatorUiState(
                loading = false,
                deckCard = cards.map { card ->
                    val order = orderMap[card.id]

                    card.copy(
                        isSelected = order != null,
                        order = order,
                    )
                }
                    .sortedWith(
                        compareBy<DFCardSlotUi> { it.order == null }
                            .thenBy { it.order }
                    ),
                name = name,
                isEditMode = mode is DeckCreatorMode.Edit
            ).also {
                Log.d("Debug", it.deckCard.toString())
            }
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

            is DeckCreatorIntent.Reorder -> reorder(intent.from, intent.to)
        }
    }

    private fun onCardCreated(cardId: Int) {

        selectedCards.update { current ->
            current + SelectedCard(
                id = cardId,
                order = current.size
            )
        }
    }


    private fun saveDeck() {
        viewModelScope.launch {
            createDeckUseCase(
                deckName.value,
                selectedCards.value
                    .map { it.id }
            )
                .onSuccess { _events.emit(DeckCreatorEvent.CloseScreen) }
                .onError { error ->
                uiEventDispatcher.dispatch(
                    UiEvent.ShowSnackBar(error.toUiMessage())
                )
            }
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

            if (mode is DeckCreatorMode.Edit) {
                viewModelScope.launch {
                    setDeckCardsUseCase(
                        mode.deckId,
                        newList.map { it.id }
                    )
                }
            }

            newList
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

    private fun reorder(from: Int, to: Int) {

        selectedCards.update { current ->

            if (from == to) return@update current

            val mutable = current.toMutableList()

            val item = mutable.removeAt(from)
            mutable.add(to, item)

            val reordered = mutable.mapIndexed { index, card ->
                card.copy(order = index)
            }

            if (mode is DeckCreatorMode.Edit) {
                viewModelScope.launch {
                    setDeckCardsUseCase(
                        mode.deckId,
                        reordered.map { it.id }
                    )
                }
            }

            reordered
        }
    }
}
