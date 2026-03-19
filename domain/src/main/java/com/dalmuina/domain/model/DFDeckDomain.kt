package com.dalmuina.domain.model

data class DFDeckDomain(
    val id: Int,
    val name: String,
    val cards : List<DFCardDomain>,
)
