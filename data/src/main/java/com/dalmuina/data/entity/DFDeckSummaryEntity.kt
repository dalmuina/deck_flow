package com.dalmuina.data.entity

import com.dalmuina.domain.model.DFDeckSummary

data class DFDeckSummaryEntity(
    val id: Int = 0,
    val name: String,
    val cardCount: Int,
)

fun DFDeckSummaryEntity.toDomain() =
    DFDeckSummary(id, name, cardCount)
