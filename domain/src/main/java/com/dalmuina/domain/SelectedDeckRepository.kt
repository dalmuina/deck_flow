package com.dalmuina.domain

import kotlinx.coroutines.flow.Flow

interface SelectedDeckRepository {

    val selectedDeckId: Flow<Int?>

    suspend fun setSelectedDeck(id: Int)
}