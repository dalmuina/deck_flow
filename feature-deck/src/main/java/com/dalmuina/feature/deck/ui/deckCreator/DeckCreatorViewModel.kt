package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.usecase.GetAllCardsUseCase
import com.dalmuina.feature.deck.ui.model.toCardUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class DeckCreatorViewModel(
    private val getAllCardsUseCase: GetAllCardsUseCase,
) : ViewModel() {

    val uiState: StateFlow<DeckCreatorUiState> =
        getAllCardsUseCase()
            .map { result ->
                when (result) {
                    is DFResult.Success -> {
                        DeckCreatorUiState(
                            loading = false,
                            deckCard = result.data.map { it.toCardUi() }
                        )
                    }

                    is DFResult.Error -> {
                        DeckCreatorUiState(
                            loading = false,
                            deckCard = emptyList()
                        )
                    }
                }
            }
            .onStart {
                emit(DeckCreatorUiState(loading = true))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DeckCreatorUiState(loading = true)
            )

    fun process(intent: DeckCreatorIntent) {
        when (intent) {
            is DeckCreatorIntent.CardClicked -> cardChanged(intent.id)
        }
    }


    private fun cardChanged(id: Int) {

    }

}