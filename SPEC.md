# DeckFlow — Spec

Defines what DeckFlow is and how it behaves: purpose, screens, MVI contracts, domain models, and the error-handling contract. For module structure, tech stack, navigation infrastructure, and the Room schema, see `ARCHITECTURE.md`.

---

## Purpose

DeckFlow is an Android **spaced repetition study** app based on flashcards.
The user creates card decks; each card has a name and a duration in seconds. During a study session, cards are presented one at a time with a countdown timer: swiping right marks the card as completed, swiping left postpones it. The stats screen shows a monthly heatmap with the activity history per card.

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

Form to create or edit a deck. The user enters the name and manages the associated card list (add, delete, reorder with drag-and-drop). `DeckCreatorMode` is `Create` or `Edit(deckId)`. There is no explicit save action: the deck is created in the background as soon as the first card is added, and every subsequent change (name, card selection, reorder) auto-persists — the same behavior in both modes.

**MVI**

| | Class |
|---|---|
| State | `DeckCreatorState` — `name`, `deckCard: List<CardUi>`, `cardPendingDelete` |
| Intent | `NameChanged`, `SelectedCard`, `CardCreated`, `RequestDeleteCard`, `ConfirmDeleteCard`, `DismissDeleteDialog`, `Reorder` |

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

---

## Error handling

`DFResult<Success, DFError>` — typed wrapper in `:domain`. `DFError` is a sealed hierarchy. Use cases always return `DFResult`. ViewModels map errors to `UiText` via helpers in `:core:presentation`.
