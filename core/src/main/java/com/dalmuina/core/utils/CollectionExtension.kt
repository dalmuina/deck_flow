package com.dalmuina.core.utils

fun List<Int>.toggleElement(id: Int): List<Int> {
    return if (contains(id)) {
        this - id
    } else {
        this + id
    }
}
