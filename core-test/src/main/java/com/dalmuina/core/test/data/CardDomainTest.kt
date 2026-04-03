package com.dalmuina.core.test.data

import com.dalmuina.domain.model.DFCardDomain


object CardDomainTestData {

    fun card(
        id: Int = 1,
        name: String = "Card $id",
        durationMillis: Long = 15000L,
        completedAt: Long? = null,
        postponedAt: Long? = null,
        order: Int? = null
    ) = DFCardDomain(
        id = id,
        name = name,
        durationMillis = durationMillis,
        completedAt  = completedAt,
        postponedAt  = postponedAt,
        order  = order,
    )

    fun cards(vararg ids: Int): List<DFCardDomain> =
        ids.map { card(id = it) }
}