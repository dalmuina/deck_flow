---
name: naming-class
description: |
  Project-specific prefix convention for Android/KMP — applies to Result wrappers, Error types, reusable Compose components and Preview annotations. Use this skill whenever naming a Result class, an Error sealed interface, a reusable design system composable, or a custom Preview annotation. Triggered directly on phrases like "project prefix", "name this component", "custom Preview annotation", or "avoid Result collision".
---

# Naming Convention — Project-Specific Prefixes

## Rule

All project-specific wrappers and reusable components are prefixed with the project's initials.

| Project | Initials | Result | Error | Component | Preview |
|---|---|---|---|---|---|
| Deck Flow | DF | `DFResult` | `DFError` | `DFOutlinedButton` | `DFPreview` |
| BitPanda | BP | `BPResult` | `BPError` | `BPOutlinedButton` | `BPPreview` |

Determine the project's initials at the start of a project and apply them consistently across the entire codebase.

---

## When to Apply

| Thing | Apply prefix | Example |
|---|---|---|
| Result wrapper | Yes | `DFResult`, `DFError`, `EmptyResult` |
| Base error interface | Yes | `DFError` |
| Reusable Compose component (`:core:design-system`) | Yes | `DFOutlinedButton`, `DFTopBar` |
| Custom Preview annotation (`:core:design-system`) | Yes | `DFPreview` |
| Feature-specific composables | No — not shared | `NoteListScreen` |
| Feature-specific error types | No — module context is enough | `PasswordValidationError` |

---

## Why

- Avoids collision with Kotlin's native `Result` and `Error` types.
- Makes project-specific classes immediately identifiable in autocomplete.
- Enforces a consistent naming contract across all modules from day one.
