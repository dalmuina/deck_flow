---
name: android-module-structure
description: Module layout, dependency rules, and Gradle convention plugins for Android and Kotlin Multiplatform (KMP) projects. Use this skill whenever setting up a new Android/KMP project, deciding where a new module should live, asking "how should I structure this", creating a new feature module, adding a core submodule, configuring Gradle convention plugins, working with version catalogs, or making any decision about project-level architecture. Trigger on phrases like "set up the project", "add a module", "create a feature", "how should I structure", "project structure", "convention plugin", "build-logic", or "where does X live".
---

# Android / KMP Module Structure

## Core Philosophy

- **Feature-layered modularization**: split by feature first, then by layer within each feature.
- **Clean Architecture layers**: `presentation` → `domain` ← `data`. Domain is innermost and depends on nothing.
- **Code lives in a feature module unless it is needed by more than one feature** — then it moves to the appropriate `core` submodule.
- Features **never depend on each other**. Cross-feature shared data belongs in `core:domain` or `core:presentation`.

---

## Module Layout

```
:app
:build-logic                    ← Gradle convention plugins
:di                             ← Koin modules
:core:domain                    ← Shared domain models, interfaces, error types, Result
:core:data                      ← Shared data logic, Ktor HttpClient factory, shared DB schemas/DAOs
:core:presentation              ← Shared UI utilities (UiText, etc.)
:core:design-system             ← Reusable Compose components, colors, theme, typography
:core:test                      ← Reusable test helpers, data, rules
:<feature>:domain               ← Feature-specific domain models, interfaces, error types
:<feature>:data                 ← Repo implementations, DTOs, mappers, Room DAOs
:<feature>:feature              ← ViewModel, screen composables, state, intents, events
```

For self-contained concerns with meaningful complexity (multiple classes, non-trivial API), create a dedicated module under `:core` (e.g., `:core:location`, `:core:analytics`). A single class or trivial utility belongs in an existing `core` module.

A shared Room database is a good candidate for `:core:database` — contains the `@Database` class, all entity definitions, all DAOs, and migrations.

---

## Dependency Rules

| Layer | May depend on |
|---|---|
| `feature` | `domain` (own feature), `core:domain`, `core:presentation`, `core:design-system` |
| `data` | `domain` (own feature), `core:domain`, `core:data` |
| `domain` | `core:domain` only — never `data` or `presentation` |
| `:app` | everything (wires all modules) |

Every layer may access `core:domain`.

---

## Convention Plugins (`:build-logic`)

| Plugin | Purpose |
|---|---|
| `android-application` | App module config |
| `android-library` | Base Android library config |
| `android-feature` | Android library + Compose + Koin + shared feature deps |
| `domain-module` | Pure Kotlin/KMP module, no Android deps |
| `compose` | Compose compiler + BOM |
| `koin` | Koin dependency block |
| `ktor` | Ktor client + serialization |
| `room` | Room + KSP config |
| `kotlinx-serialization` | KotlinX Serialization plugin + dep |

Use **version catalogs** (`libs.versions.toml`) for all dependency and version management.

---

## Key Libraries

| Concern | Library |
|---|---|
| DI | Koin |
| Networking | Ktor Client |
| Local DB | Room |
| Preferences | DataStore |
| Navigation | Navigation3 |
| Serialization | KotlinX Serialization |
| Image loading | Coil |
| Logging | Kermit |
| Async | Coroutines + Flow |
| Background tasks | WorkManager |
| Secrets | `local.properties` + `BuildConfig` (Android); `BuildKonfig` (KMP) |
| Testing | JUnit4, Turbine, `kotlinx-coroutines-test`, Kotest, MockK |
| UI testing | `ComposeTestRule` |

---

## Checklist: Adding a New Feature Module

- [ ] Create `:<feature>:domain`, `:<feature>:data`, `:<feature>:feature` modules
- [ ] Apply appropriate convention plugins (`domain-module`, `android-library`/`android-feature`)
- [ ] Verify no cross-feature dependencies are introduced
- [ ] If logic is shared across 2+ features, extract to the appropriate `core` submodule
