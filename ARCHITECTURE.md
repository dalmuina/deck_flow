# DeckFlow — Architecture

Defines how DeckFlow is built: modules, dependency rules, tech stack, navigation infrastructure, and the Room schema. For what the app does — purpose, screens, MVI contracts, domain models, error-handling contract — see `SPEC.md`.

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

| Area | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose BOM |
| DI | Koin (Android + Compose) |
| Database | Room |
| Preferences | DataStore Preferences |
| Navigation | Navigation3 |
| Network | Ktor Client Android |
| Coroutines | kotlinx-coroutines |
| Annotations | KSP |
| Crash reporting | Firebase Crashlytics |
| Analytics | Firebase Analytics |
| Leak detection | LeakCanary (debug) |
| Reordering | Reorderable |
| Tests | JUnit4 + Kotest + MockK + Turbine |
| Lint | Detekt |
| Desugaring | Core Library Desugaring |
| minSdk / targetSdk | 26 / 37 |

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

## Room schema (simplified)

```
cards               ← CardEntity
decks               ← DeckEntity
deck_card_cross_ref ← DeckCardCrossEntity (many-to-many with order)
card_progress       ← CardProgressEntity  (completedAt, postponeAt)
card_history        ← CardHistoryEntity   (session history)
daily_stats         ← DailyStatsEntity
```
