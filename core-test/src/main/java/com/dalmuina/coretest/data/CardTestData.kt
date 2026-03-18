package com.dalmuina.coretest.data

import com.dalmuina.domain.model.DFCardDomain

object CardTestData {

    fun card(
        id: Int = 1,
        name: String = "Card $id",
        durationMillis: Long = 15000L
    ) = DFCardDomain(
        id = id,
        name = name,
        durationMillis = durationMillis
    )

    fun cards(vararg ids: Int): List<DFCardDomain> =
        ids.map { card(id = it) }
}
