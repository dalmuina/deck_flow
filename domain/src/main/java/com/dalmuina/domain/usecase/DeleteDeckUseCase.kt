package com.dalmuina.domain.usecase

import com.dalmuina.domain.DeckLocalDataSource
import com.dalmuina.domain.SelectedDeckDataSource
import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DeleteDeckUseCase(
    private val repository: DeckLocalDataSource,
    private val selectedDeckRepository: SelectedDeckDataSource,
    private val dispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(deckId: Int): DFResult<Int?, DataError> =
        withContext(dispatcher) {

            when (val decksResult = repository.getAllDecksWithCards().first()) {
                is DFResult.Error -> {
                    DFResult.Error(decksResult.error)
                }

                is DFResult.Success -> {
                    val decks = decksResult.data

                    when (val selectedIdResult = selectedDeckRepository.selectedDeckId.first()) {
                        is DFResult.Error -> {
                            DFResult.Error(selectedIdResult.error)
                        }

                        is DFResult.Success -> {
                            val selectedId = selectedIdResult.data

                            val nextDeck =
                                if (selectedId == deckId)
                                    calculateNextDeck(deckId, decks.map { it.id })
                                else null

                            when (val deleteResult = repository.deleteDeck(deckId)) {
                                is DFResult.Error ->
                                    DFResult.Error(deleteResult.error)

                                is DFResult.Success ->
                                    DFResult.Success(nextDeck)
                            }
                        }
                    }
                }
            }
        }

    private fun calculateNextDeck(
        deletedId: Int,
        decks: List<Int>
    ): Int? {
        val index = decks.indexOf(deletedId)

        if (index == -1) return null

        return decks.getOrNull(index + 1)
            ?: decks.getOrNull(index - 1)
    }
}
