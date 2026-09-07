package com.dalmuina.domain.helpers

import com.dalmuina.domain.model.CardDomain

fun List<CardDomain>.sortedForSession(): List<CardDomain> =
    this
        .sortedWith(
            compareBy<CardDomain> { card ->
                when {
                    card.completedAt != null -> 2
                    card.postponedAt != null -> 1
                    else -> 0
                }
            }.thenBy { card ->
                card.postponedAt ?: Long.MIN_VALUE
            }.thenBy { card ->
                card.order ?: Int.MAX_VALUE
            },
        )
