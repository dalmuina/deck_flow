package com.dalmuina.core.test.data

import com.dalmuina.domain.model.CardDomain

object CardDomainTestData {
    fun card(
        id: Int = 1,
        name: String = "Card $id",
        durationMillis: Long = 15000L,
        completedAt: Long? = null,
        postponedAt: Long? = null,
        order: Int? = null,
        description: String? = null,
    ) = CardDomain(
        id = id,
        name = name,
        durationMillis = durationMillis,
        completedAt = completedAt,
        postponedAt = postponedAt,
        order = order,
        description = description,
    )

    fun cards(vararg ids: Int): List<CardDomain> = ids.map { card(id = it) }
}
