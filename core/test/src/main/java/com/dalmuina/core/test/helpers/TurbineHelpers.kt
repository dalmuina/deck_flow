package com.dalmuina.core.test.helpers

import app.cash.turbine.TurbineTestContext

suspend fun <T> TurbineTestContext<T>.awaitLoaded(): T {
    awaitItem()
    return awaitItem()
}