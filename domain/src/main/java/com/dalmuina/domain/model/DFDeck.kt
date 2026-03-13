package com.dalmuina.domain.model

data class DFDeck(
    val id: Int,
    val name: String,
    val cards : List<DFCard>,
)
