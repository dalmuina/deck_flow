# DeckFlow — Android Project

## Working Principles

**Think before coding.** State your assumptions out loud. If the request is ambiguous, ask. If a simpler approach exists, push back. Stop when you are confused, name what is unclear, do not just pick one interpretation and run.

**Simplicity first.** Write the minimum code that solves the problem. No speculative abstractions. No flexibility nobody asked for. Idiomatic over clever — a reader unfamiliar with the feature should follow it without effort.

**Surgical changes.** Touch only what the task requires. Do not improve neighboring code. Do not refactor what is not broken. Every changed line should trace back to the request. If you spot a defect in untouched code, flag it — do not fix it silently.

**Goal-driven execution.** Turn vague instructions into verifiable targets before writing a line. "Add validation" becomes "write tests for invalid inputs, then make them pass."

---

## Purpose

DeckFlow is an Android **spaced repetition study** app based on flashcards.
The user creates card decks; each card has a name and a duration in seconds. During a study session, cards are presented one at a time with a countdown timer: swiping right marks the card as completed, swiping left postpones it. The stats screen shows a monthly heatmap with the activity history per card.

---

## Build

```
./gradlew assembleDebug       # build debug
./gradlew test                # unit tests
./gradlew ktlintCheck         # lint
```

---

## Architecture and modules

The project uses a **modular layered architecture** with a single Activity and Compose as the UI.

```
:app                    → Entry point, navigation, Koin init, Firebase
:core:presentation      → UiEventDispatcher, error-to-UiText mappers
:core:design-system     → Theme, color tokens, reusable DF components
:core:data              → Safe-call helpers (Room, DataStore)
:core:domain            → Clock, date utilities
:core:test              → Test data factories
:feature-card           → Study session: cards + timer
:feature-deck           → Deck and card management
:feature-stats          → Stats and heatmap
:domain                 → Domain models, DataSource interfaces, use cases
:data                   → Room DB, DAOs, DataStore, DataSource implementations
:di                     → Koin modules for all layers
```

### Dependency rule

`feature-*` → `:domain` ← `:data` ← `:di` → `:app`

Feature modules never depend on `:data` directly.

---

## Tech stack

| Area | Library | Version |
|---|---|---|
| Language | Kotlin | 2.3.21 |
| UI | Jetpack Compose BOM | 2026.04.01 |
| DI | Koin (Android + Compose) | 4.2.1 |
| Database | Room | 2.8.4 |
| Preferences | DataStore Preferences | 1.2.1 |
| Navigation | Navigation3 | 1.1.1 |
| Network | Ktor Client Android | 3.4.3 |
| Coroutines | kotlinx-coroutines | 1.10.2 |
| Annotations | KSP | 2.3.6 |
| Crash reporting | Firebase Crashlytics | plugin 3.0.7 |
| Analytics | Firebase Analytics | BOM 34.12.0 |
| Leak detection | LeakCanary (debug) | 2.14 |
| Reordering | Reorderable | 3.1.0 |
| Tests | JUnit4 + Kotest + MockK + Turbine | — |
| Lint | Detekt | 1.23.8 |
| Desugaring | Core Library Desugaring | 2.1.5 |
| minSdk / targetSdk | 26 / 36 | — |

---

## Navigation

Navigation3 with serializable routes and **per-tab multi-stack**.

```kotlin
sealed interface Route : NavKey {
    data object Card : Route            // Routine tab
    data object DeckSelector : Route    // Decks tab
    data class DeckCreator(val mode: DeckCreatorMode) : Route
    data class CardCreator(val mode: CardCreatorMode) : Route
    data object Stats : Route           // Stats tab
}
```

- `Navigator` encapsulates all navigation actions.
- `NavigationState` maintains the back-stack per tab.
- `NavDisplay` in `:app` registers each route with `entryProvider`.
- `ViewModelStore` and `SavedState` are preserved across rotations.

---

## Screens

### Study session — `Route.Card` → `CardRoute`

The main daily-use screen. Shows the cards of the selected deck ordered by priority. Each card displays its name and a countdown timer (`TimerForegroundService`). The user swipes the card:
- **Right** → completes the card (`CompleteCardUseCase`)
- **Left** → postpones the card (`PostponeCardUseCase`)

Timer state is persisted in DataStore to survive process death.

**MVI**

| | Class |
|---|---|
| State | `CardState` — `loading`, `name`, `cards: List<CardUi>`, `isDeckSelected` |
| Intent | `SwipeTopCard(direction, total)` |
| ViewModels | `CardViewModel`, `TimerViewModel` |

---

### Deck selector — `Route.DeckSelector` → `DeckSelectorRoute`

Lists all available decks. The user selects the active deck to use in the session. Allows deleting decks with confirmation (dialog) and navigates to `DeckCreator` for editing.

**MVI**

| | Class |
|---|---|
| State | `DeckSelectorState` — `loading`, `deckList: List<DeckUi>`, `deckPendingDelete` |
| Intent | `SelectDeck`, `RequestDeleteDeck`, `ConfirmDeleteDeck`, `DismissDeleteDialog` |

---

### Deck creator / editor — `Route.DeckCreator(mode)` → `DeckCreatorRoute`

Form to create or edit a deck. The user enters the name and manages the associated card list (add, delete, reorder with drag-and-drop). `DeckCreatorMode` is `Create` or `Edit(deckId)`.

**MVI**

| | Class |
|---|---|
| State | `DeckCreatorState` — `name`, `deckCard: List<CardUi>`, `isEditMode`, `cardPendingDelete` |
| Intent | `NameChanged`, `SaveDeck`, `SelectedCard`, `CardCreated`, `RequestDeleteCard`, `ConfirmDeleteCard`, `DismissDeleteDialog`, `Reorder` |
| Event | `CloseScreen` |

---

### Card creator / editor — `Route.CardCreator(mode)` → `CardCreatorDialogNavRoute`

Modal dialog to create or edit an individual card. The user enters the name and duration (with `+`/`-` buttons or a text field). `CardCreatorMode` is `Create` or `Edit(cardId)`.

**MVI**

| | Class |
|---|---|
| State | `CardCreatorState` — `name`, `duration: Duration`, `processing` |
| Intent | `NameChanged`, `TimeChanged`, `MoreTime`, `LessTime`, `SaveActivity`, `Cancel` |
| Event | `CloseScreen(cardId?)` |

---

### Stats — `Route.Stats` → `StatsRoute`

Shows a **monthly heatmap** (GitHub contribution graph style) with the activity history for a selected card. The user can navigate between months and filter by card. Data comes from `CardHistoryEntity`.

**MVI**

| | Class |
|---|---|
| State | `StatsState` — `activityOptions`, `selectedCardId`, `year`, `month`, `heatmapDays`, `hasPreviousData`, `hasNextMonth` |
| Intent | `SelectActivity(cardId)`, `PreviousMonth`, `NextMonth` |

---

## Key domain models

```kotlin
data class CardDomain(val id: Int, val name: String, val durationMillis: Long,
                      val completedAt: Long?, val postponedAt: Long?, val order: Int?)

data class DeckDomain(val id: Int, val name: String, val cards: List<CardDomain>)

data class DailyStatsDomain(val dayStart: Long, val totalSpentMillis: Long, val completedCount: Int)

data class PersistedTimerState(val totalMillis: Long, val remainingMillis: Long,
                               val isRunning: Boolean, val endTimeMillis: Long?)
```

### Room schema (simplified)

```
cards               ← CardEntity
decks               ← DeckEntity
deck_card_cross_ref ← DeckCardCrossEntity (many-to-many with order)
card_progress       ← CardProgressEntity  (completedAt, postponeAt)
card_history        ← CardHistoryEntity   (session history)
daily_stats         ← DailyStatsEntity
```

---

## Error handling

`DFResult<Success, DFError>` — typed wrapper in `:domain`. `DFError` is a sealed hierarchy. Use cases always return `DFResult`. ViewModels map errors to `UiText` via helpers in `:core:presentation`.

---

## Conventions

- Project prefix: **DF** → `DFResult`, `DFError`, `DFPreview`, `DFNavigationBar`…
- **Never** use `Impl` suffix — name by what makes the class unique
- `@Stable` on all State and UI model data classes
- `@Immutable` on UI model lists
- `IO` dispatcher injected with `named("IO")` in use cases
- Follow the skills in `.claude/skills/` for architecture, naming, and patterns
