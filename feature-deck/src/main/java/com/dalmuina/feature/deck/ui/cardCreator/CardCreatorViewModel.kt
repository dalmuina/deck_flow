package com.dalmuina.feature.deck.ui.cardCreator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CardCreatorViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(CardCreatorUiState())
    val uiState: StateFlow<CardCreatorUiState> = _uiState.asStateFlow()



    fun process(intent: CardCreatorIntent){
        when(intent){
            is CardCreatorIntent.TitleChanged -> changeTitle(intent.value)
            is CardCreatorIntent.TimeChanged ->
                adjustMinutes { intent.value }

            CardCreatorIntent.MoreTime ->
                adjustMinutes {(it + 1).coerceAtLeast(0).toString()}

            CardCreatorIntent.LessTime ->
                adjustMinutes {(it -1).coerceAtLeast(0).toString()}
        }
    }

    private fun changeTitle(value: String){
        reduce {
            copy(
                title = value
            )
        }
    }

    private fun adjustMinutes(transform:(Int)->String) {
        reduce {
            val current = minutes.toIntOrNull() ?: 0
            copy(minutes = transform(current))
        }
    }

    private inline fun reduce(
        reducer: CardCreatorUiState.() -> CardCreatorUiState
    ) {
        _uiState.update {
            it.reducer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("Viewmodel CardCreator cleaned")
    }
}