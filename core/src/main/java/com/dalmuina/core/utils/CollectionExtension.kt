package com.dalmuina.core.utils

fun Set<Int>.toggleElement(id: Int): Set<Int> {
    return if (contains(id)) {
        this - id
    } else {
        this + id
    }
}
