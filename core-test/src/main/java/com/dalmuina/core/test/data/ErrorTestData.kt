package com.dalmuina.core.test.data

import com.dalmuina.domain.model.DataError

object ErrorTestData {

    val unknown = DataError.Local.Unknown(RuntimeException("Unknown database error"))

}