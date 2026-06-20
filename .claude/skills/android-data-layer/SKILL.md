---
name: android-data-layer
description: |
 Data layer patterns for Android/KMP - data sources, repositories, DTOs, mappers, Room entities, Ktor HttpClient, safe call helpers, token storage, and offline-first. Use this skill whenever writing or reviewing a data source or repository, creating DTOs or Room entities, writing mappers, setting up the Ktor HttpClient, handling network errors, or implementing token refresh. Trigger on phrases like "create a repository", "create a data source", "add a DAO", "Ktor client", "write a mapper", "DTO", "Room entity", "network call", "token storage", or "offline-first".
---

# Android / KMP Data Layer

## Error Handling

Uses `Result<T, E>`, `DataError`, and the safe call helpers defined in the **android-error-handling** skill. Refer to that skill for the full `Result` wrapper, `DataError` sealed interface, `safeDbCall`, `safeNetworkCall`, and extension helpers.

---

## Data Source vs Repository

- **Data source** — wraps a single source (local DB, remote API, DataStore). Most classes in the data layer are data sources.
- **Repository** — coordinates multiple sources (e.g., remote API + local DB for offline-first). Only use "repository" when the class genuinely combines sources.

```kotlin
// Single source → data source
interface NoteLocalDataSource {
   suspend fun getNotes(): Result<List<Note>, DataError>
   suspend fun insertNote(note: Note): EmptyResult<DataError>
}

interface NoteRemoteDataSource {
   suspend fun fetchNotes(): Result<List<Note>, DataError>
}

// Multiple sources → repository
interface NoteRepository {
   suspend fun getNotes(): Result<List<Note>, DataError>
   suspend fun sync(): EmptyResult<DataError>
}
```

---

## Domain Layer Contracts

- Pure Kotlin — no Android/framework imports.
- Contains: domain models, data source/repository **interfaces**, error types, result interface.
- Every data source or repository used by a ViewModel must have an interface in `domain` — keeps `presentation` decoupled from `data` and enables testing.

---

## DTOs and Domain Models

- Always separate: DTOs (data layer) ↔ Domain Models (domain layer).
- Domain models never go directly into Room entities or Ktor request/response bodies.
- Mappers are extension functions in the data layer, defined alongside the DTO they transform: `NoteDto.toDomain()`, `Note.toDto()`, `NoteEntity.toDomain()`, `Note.toEntity()`, `Note.toUi()`.

---

## Implementations

Name implementations for what makes them unique — never suffix with `Impl`.

### Data source (single source)

Suspend functions use `safeDbCall { dao.method() }`. Flows use `map + catch` directly on the DAO Flow — never wrap a Flow in `safeDbCall`:

```kotlin
override fun getNotesFlow(): Flow<Result<List<Note>, DataError>> =
    dao.getAllNotes()
        .map { entities ->
            Result.Success(entities.map { it.toDomain() })
                    as Result<List<Note>, DataError>
        }
        .catch { e ->
            if (e is CancellationException) throw e
            emit(Result.Error(DataError.Local.Unknown(e)))
        }
```

### Repository (multiple sources)

`onSuccess`/`onFailure` lambdas are **non-suspend** — never call DataSource functions inside them. Use `when{}`:

```kotlin
class OfflineFirstNoteRepository(
   private val localDataSource: NoteLocalDataSource,
   private val remoteDataSource: NoteRemoteDataSource
) : NoteRepository {

   override suspend fun getNotes(): Result<List<Note>, DataError> {
       return when (val remote = remoteDataSource.fetchNotes()) {
           is Result.Success -> {
               localDataSource.insertAll(remote.data)
               remote
           }
           is Result.Error -> localDataSource.getNotes()
       }
   }

   override suspend fun sync(): EmptyResult<DataError> {
       return when (val remote = remoteDataSource.fetchNotes()) {
           is Result.Success -> localDataSource.insertAll(remote.data).asEmptyResult()
           is Result.Error -> Result.Error(remote.error)
       }
   }
}
```

### Offline-first: expose Room Flow

The ViewModel observes only the local Room Flow. Sync writes to Room, which triggers the Flow automatically:

```kotlin
// Repository exposes local Flow — never the network response directly
override fun getNotesFlow(): Flow<Result<List<Note>, DataError>> =
   localDataSource.getNotesFlow()

// Sync called from ViewModel init or WorkManager
override suspend fun sync(): EmptyResult<DataError> {
   return when (val remote = remoteDataSource.fetchNotes()) {
       is Result.Success -> localDataSource.insertAll(remote.data).asEmptyResult()
       is Result.Error -> Result.Error(remote.error)
   }
}
```

---

## Ktor — Network Client (`:network`)

Network files live in a dedicated `:network` module. Create `HttpClient` with the OkHttp engine: set connect/read/write timeouts from a config object, add an `Interceptor` to inject API keys as query parameters, and install `Logging` (body level) and `ContentNegotiation` with `json { ignoreUnknownKeys = true; isLenient = true }`. Inject `HttpClient` via Koin. See **android-error-handling** for `NetworkClient`, `safeNetworkCall`, and `mapHttpCodeToNetworkError`.

---

## Token Storage

Store tokens in DataStore (in `core:data` or `:core:auth`). The Ktor `Auth` plugin reads/writes tokens and handles 401 refresh automatically.

---

## Room Migrations

Prefer `@Database(autoMigrations = [AutoMigration(from = 1, to = 2)])`. Use manual `Migration` objects only when the schema change is too complex for auto-migration.

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Data source interface | `<Entity><Local/Remote>DataSource` | `NoteLocalDataSource` |
| Data source impl | what makes it unique | `RoomNoteDataSource`, `KtorNoteDataSource` |
| Repository interface | `<Entity>Repository` | `NoteRepository` |
| Repository impl | what makes it unique | `OfflineFirstNoteRepository` |
| DTO | `<Model>Dto` | `NoteDto` |
| Room entity | `<Model>Entity` | `NoteEntity` |
| Mapper | extension fun on source type | `fun NoteDto.toDomain()` |

---

## Checklist: Adding a New Data Source or Repository

- [ ] Define domain model(s) in `domain`
- [ ] Define data source or repository interface in `domain`
- [ ] Define feature-specific error type(s) in `domain`
- [ ] Define DTOs and Room entities in `data`
- [ ] Write mappers as extension functions in `data`
- [ ] Implement data source (single source) or repository (multi-source), named for what makes it unique
