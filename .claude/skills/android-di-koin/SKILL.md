---
name: android-di-koin
description: |
  Koin dependency injection setup for Android/KMP - module definitions per layer, ViewModel injection, assembling modules in :app, and koinViewModel() in composables. Use this skill whenever setting up Koin, defining a DI module, providing a repository or ViewModel, injecting a dependency, or wiring modules in the Application class. Trigger on phrases like "set up Koin", "add a Koin module", "inject a dependency", "DI module", "koinViewModel", "provide a ViewModel", "startKoin", or "single/viewModel/factory".
---

# Android / KMP Dependency Injection (Koin)

## Principles

- One Koin module per feature layer — create only if there are dependencies to provide.
- Modules are assembled in `:app`, never in feature modules themselves.
- Modules live in `:di`.
- In Route composables, always inject ViewModels via `koinViewModel()`.

---

## Module Definitions

Prefer constructor-reference overloads (`singleOf`, `viewModelOf`, `factoryOf`) — they are more concise and let Koin resolve parameters automatically. Fall back to lambda overloads (`single { }`, `viewModel { }`, `factory { }`) only when a factory method, named qualifier, or post-construction setup is needed.

```kotlin
// :di — data layer
val notesDataModule = module {
    singleOf(::RoomNoteDataSource) { bind<NoteLocalDataSource>() }
    singleOf(::KtorNoteDataSource) { bind<NoteRemoteDataSource>() }
    singleOf(::OfflineFirstNoteRepository) { bind<NoteRepository>() }

    // Lambda overload needed — calling a factory method, not a constructor
    single { HttpClientFactory.create(get()) }
    single { createDataStore(get()) }
}

// :di — presentation layer
val notesPresentationModule = module {
    viewModelOf(::NoteListViewModel)
    viewModelOf(::NoteDetailViewModel)
}
```

---

## Assembly in `:app`

Call `startKoin { androidContext(this@App); modules(...) }` in `Application.onCreate()`, listing every module from `:di`.

---

## Injecting in Composables

Always use `koinViewModel()` in Route composables. Never pass ViewModels down the composable tree:

```kotlin
@Composable
fun NoteListRoute(
    onNavigateToDetail: (String) -> Unit,
    viewModel: NoteListViewModel = koinViewModel(),
) { ... }
```

---

## Scoping Rules

| Scope | Preferred form | When to use |
|---|---|---|
| Singleton | `singleOf(::MyRepository) { bind<MyInterface>() }` | One instance for the app lifetime (repositories, HttpClient, DB) |
| ViewModel | `viewModelOf(::MyViewModel)` | ViewModel instances scoped to their lifecycle |
| Factory | `factoryOf(::MyClass)` | New instance on every injection (rare) |

---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Koin module | `<feature><Layer>Module` | `notesDataModule`, `notesPresentationModule` |

---

## Checklist: Adding DI for a New Feature

- [ ] Define `val <feature>DataModule = module { ... }` in `:di`
- [ ] Define `val <feature>PresentationModule = module { ... }` in `:di`
- [ ] Register both in `:app`'s `startKoin { modules(...) }`
- [ ] Use `koinViewModel()` in all Route composables
