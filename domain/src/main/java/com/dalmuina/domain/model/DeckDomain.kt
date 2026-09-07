package com.dalmuina.domain.model

data class DeckDomain(
    val id: Int,
    val name: String,
    val cards: List<CardDomain>,
)
