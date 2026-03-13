package com.dalmuina.coretest.data

import com.dalmuina.domain.model.DFCard

object CardTestData {

    fun card(
        id: Int = 1,
        name: String = "Card $id",
        durationMillis: Long = 15000L
    ) = DFCard(
        id = id,
        name = name,
        durationMillis = durationMillis
    )

    fun cards(vararg ids: Int): List<DFCard> =
        ids.map { card(id = it) }
}
