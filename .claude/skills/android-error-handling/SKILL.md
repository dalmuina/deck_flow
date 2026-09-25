---
name: android-error-handling
description: |
 Generic Result wrapper, error types, and extension helpers for Android/KMP - Result<T, E>, DataError, EmptyResult, map, onSuccess, onFailure. Use this skill whenever defining error types, creating a Result wrapper, handling success/failure flows, mapping errors, or working with typed errors anywhere in the app (not just data layer — also validation, auth, domain logic). Trigger on phrases like "Result wrapper", "error handling", "DataError", "onSuccess", "onFailure", "EmptyResult", "map result", "error type", "validation error", or "typed errors".
---

# Android / KMP Error Handling

## Result Wrapper (`:domain`)

Follow the **naming-class** skill to prefix `Result` and `Error` with the project initials (e.g. `DFResult`, `DFError`).

```kotlin
interface Error

sealed interface Result<out D, out E : Error> {
   data class Success<out D>(val data: D) : Result<D, Nothing>
   data class Error<out E : Error>(val error: E) : Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>
```

---

## Extension Helpers (`:domain`)

> **Important:** `onSuccess` and `onFailure` lambdas are **non-suspend**. Never call suspend functions (DataSource, Repository, DAO) inside them. In a Repository, use `when{}` instead — see the **android-data-layer** skill.

```kotlin
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> =
   when (this) {
       is Result.Error -> Result.Error(error)
       is Result.Success -> Result.Success(map(this.data))
   }

// WARNING: non-suspend — only for non-suspend side effects (logging, in-memory state)
inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> =
   when (this) {
       is Result.Error -> this
       is Result.Success -> { action(this.data); this }
   }

// WARNING: non-suspend — only for non-suspend side effects (logging, in-memory state)
inline fun <T, E : Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> =
   when (this) {
       is Result.Error -> { action(error); this }
       is Result.Success -> this
   }

fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E> = map { }
```

Valid — non-suspend side effects only:
```kotlin
val result = repository.saveNote(note)
   .onSuccess { Timber.d("Saved ${it.id}") }
   .onFailure { Timber.e("Save failed: $it") }
   .asEmptyResult()
```

Invalid — suspend call inside lambda does not compile:
```kotlin
// DOES NOT COMPILE — insertAll() is suspend, onSuccess lambda is not
remoteDataSource.fetchNotes()
   .onSuccess { notes -> localDataSource.insertAll(notes) }
```

---

## Shared Error Types (`core:domain`)

`DataError` is a sealed interface with three nested sealed interfaces:
- `DataError.Network` — `BadRequest`, `RequestTimeout`, `Unauthorized`, `Forbidden`, `NotFound`, `Conflict`, `TooManyRequests`, `NoInternet`, `PayloadTooLarge`, `ServerError`, `ServiceUnavailable`, `Serialization`, `Unknown(throwable)`
- `DataError.Local` — `DiskFull`, `NotFound`, `ConstraintViolation`, `Unknown(throwable)`
- `DataError.Preferences` — `WriteError`, `ReadError`, `Unknown(throwable)`

### Feature-Specific Errors

Features define their own error types by implementing `Error`. Always return a single error per `Result`:
```kotlin
sealed interface PasswordValidationError : Error {
   data object TooShort : PasswordValidationError
   data object NoUpperCase : PasswordValidationError
   data object NoDigit : PasswordValidationError
}
```

---

## Exception Handling Philosophy

Never throw exceptions for expected failures — always return `Result.Error`. Catch at the layer responsible:

| Exception origin | Catch in | Example |
|---|---|---|
| HTTP / network | Data layer | `UnknownHostException` → `DataError.Network.NoInternet` |
| Database / disk | Data layer | `SQLiteFullException` → `DataError.Local.DiskFull` |
| DataStore | Data layer | `IOException` → `DataError.Preferences.WriteError` |
| Business logic | Domain layer | Invalid input → `Result.Error(ValidationError.TooShort)` |

---

## Mapping Errors to UiText

- **Feature's `presentation` module** — for feature-specific errors (e.g., `AuthError.toUiText()`)
- **`:core-presentation`** — for shared errors (e.g., `DataError.toUiText()`)

```kotlin
fun DataError.toUiText(): UiText = when (this) {
   DataError.Network.NoInternet -> UiText.StringResource(R.string.error_no_internet)
   DataError.Network.ServerError -> UiText.StringResource(R.string.error_server)
   DataError.Local.DiskFull -> UiText.StringResource(R.string.error_disk_full)
   else -> UiText.StringResource(R.string.error_unknown)
}
```

---

## Safe Call Helpers (`core-data`)

### Network

```kotlin
interface NetworkClient {
    suspend fun <T : Any> executeGet(route: String, typeInfo: TypeInfo): T
}

suspend inline fun <reified T : Any> NetworkClient.get(route: String): T =
    executeGet(route, typeInfo<T>())

class KtorNetworkClient(private val client: HttpClient) : NetworkClient {
    override suspend fun <T : Any> executeGet(route: String, typeInfo: TypeInfo): T =
        client.get(constructRoute(route)).body(typeInfo)
}

suspend inline fun <T> safeNetworkCall(
   crossinline call: suspend () -> T
): Result<T, DataError> = try {
   Result.Success(call())
} catch (e: CancellationException) {
   throw e
} catch (e: SocketTimeoutException) {
   Result.Error(DataError.Network.RequestTimeout)
} catch (e: UnknownHostException) {
   Result.Error(DataError.Network.NoInternet)
} catch (e: SerializationException) {
   Result.Error(DataError.Network.Serialization)
} catch (e: RedirectResponseException) {
   Result.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
} catch (e: ClientRequestException) {
   Result.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
} catch (e: ServerResponseException) {
   Result.Error(mapHttpCodeToNetworkError(e.response.status.value, e))
} catch (e: Exception) {
   Result.Error(DataError.Network.Unknown(e))
}

// Maps HTTP codes: 400→BadRequest, 401→Unauthorized, 403→Forbidden, 404→NotFound,
// 408→RequestTimeout, 409→Conflict, 413→PayloadTooLarge, 429→TooManyRequests,
// 500→ServerError, 503→ServiceUnavailable, else→Unknown(throwable)
fun mapHttpCodeToNetworkError(code: Int, throwable: Throwable? = null): DataError
```

### Local (Room)

Use `safeDbCall` for suspend functions only. For Flows, use `map + catch` directly on the DAO Flow.

```kotlin
suspend inline fun <reified T> safeDbCall(
   crossinline call: suspend () -> T
): Result<T, DataError> = try {
   Result.Success(call())
} catch (e: CancellationException) {
   throw e
} catch (e: SQLiteConstraintException) {
   Result.Error(DataError.Local.ConstraintViolation)
} catch (e: Exception) {
   Result.Error(DataError.Local.Unknown(e))
}
```

Usage:
```kotlin
// suspend — wrap with safeDbCall
override suspend fun getCardById(id: Int): Result<Card, DataError> =
   safeDbCall { dao.getCardById(id).toDomain() }

// Flow — map + catch directly
override fun getAllCards(): Flow<Result<List<Card>, DataError>> =
   dao.getAllCards()
       .map { Result.Success(it.map { e -> e.toDomain() }) as Result<List<Card>, DataError> }
       .catch { e ->
           if (e is CancellationException) throw e
           emit(Result.Error(DataError.Local.Unknown(e)))
       }
```

---

## When to Use What

| Scenario | Error type |
|---|---|
| Network call | `DataError` |
| Local DB access | `DataError` |
| DataStore access | `DataError` (`DataError.Preferences` subtype) |
| Repository (multi-source) | `DataError` (supertype) |
| Domain validation | Custom `Error` |
