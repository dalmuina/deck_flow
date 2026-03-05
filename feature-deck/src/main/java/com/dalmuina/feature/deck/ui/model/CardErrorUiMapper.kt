package com.dalmuina.feature.deck.ui.model

import com.dalmuina.domain.model.DataBaseError
import com.dalmuina.feature.deck.R

fun DataBaseError.toUiMessage(): Int {
    return when (this) {

        DataBaseError.ConstraintViolation ->
            R.string.error_constraint

        is DataBaseError.Unknown ->
            R.string.error_unknown
    }
}