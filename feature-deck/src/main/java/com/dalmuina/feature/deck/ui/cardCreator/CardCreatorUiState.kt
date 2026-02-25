package com.dalmuina.feature.deck.ui.cardCreator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toSet

data class CardCreatorUiState(
    val loading: Boolean = false,
    val title: String = "",
    val minutes: String = "0"
)

data class User(
    val name: String
)

sealed interface SearchResult {
    object Idle : SearchResult
    object Loading : SearchResult
    data class Success(val users: List<User>) : SearchResult
    data class Error(val message: String) : SearchResult
}

suspend fun fetchUsers(query: String): List<User> {
    return emptyList()
}

fun main() {

    fun search(queryFlow: Flow<String>): Flow<SearchResult> {
        return queryFlow
            .debounce(300)
            .filter { it.isNotEmpty() }
            .flatMapLatest {
                flow {
                    emit(SearchResult.Loading )
                    try {
                       val value = (fetchUsers(it))
                        emit(SearchResult.Success(value))
                    } catch (e: Exception) {
                        emit(SearchResult.Error(e.message.toString()))
                    }
                }
            }.distinctUntilChanged()
    }
}
