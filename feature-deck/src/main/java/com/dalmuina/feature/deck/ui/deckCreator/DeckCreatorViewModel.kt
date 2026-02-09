package com.dalmuina.feature.deck.ui.deckCreator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeckCreatorViewModel : ViewModel() {

    private val _todos = MutableStateFlow(
        (1..100).map{"Todo $it"}
    )
    val todos  = _todos.asStateFlow()
}