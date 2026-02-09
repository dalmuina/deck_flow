package com.dalmuina.feature.deck.ui.deckSelector

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeckSelectorViewModel : ViewModel() {

    private val _todos = MutableStateFlow(
        (1..5).map{"Todo $it"}
    )
    val todos  = _todos.asStateFlow()
}