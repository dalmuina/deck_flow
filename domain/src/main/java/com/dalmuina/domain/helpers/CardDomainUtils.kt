package com.dalmuina.domain.helpers

import com.dalmuina.domain.model.DFCardDomain

fun List<DFCardDomain>.sortedForSession(): List<DFCardDomain> {
    return this
        .sortedWith(
            compareBy<DFCardDomain> { card ->
                when {
                    card.completedAt != null -> 2
                    card.postponedAt != null -> 1
                    else -> 0
                }
            }.thenBy { card ->
                card.order ?: Int.MAX_VALUE
            }
        )
}