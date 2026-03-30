package com.dalmuina.data.repository

import com.dalmuina.domain.model.DFResult
import com.dalmuina.domain.model.DataError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend inline fun <T> safeNetworkCall(
    crossinline call: suspend () -> T
): DFResult<T, DataError.Network> {
    return try {
        DFResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: SocketTimeoutException) {
        DFResult.Error(DataError.Network.RequestTimeout)
    } catch (e: UnknownHostException) {
        DFResult.Error(DataError.Network.NoInternet)
    } catch (e: SerializationException) {
        DFResult.Error(DataError.Network.Serialization)
    } catch (e: RedirectResponseException) {
        DFResult.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
    } catch (e: ClientRequestException) {
        DFResult.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
    } catch (e: ServerResponseException) {
        DFResult.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
    } catch (e: ResponseException) {
        DFResult.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
    } catch (e: Exception) {
        DFResult.Error(DataError.Network.Unknown(e))
    }
}

