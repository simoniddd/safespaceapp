# SafeSpace

An Android journaling app that assembles your day automatically — expenses, calendar events, sleep, steps, workouts, and a daily mood — into a single readable timeline. You don't *track* your day; you *read* it in the evening.

## Table of Contents

- [Overview](#overview)
- [Design Principles](#design-principles)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Roadmap](#roadmap)
- [Privacy](#privacy)
- [License](#license)

## Overview

SafeSpace pulls context from passive sources — the system calendar, Health Connect, and parsed bank push notifications — and composes them into a chronological, heterogeneous feed. Manual input is minimized by design; when it exists, it takes seconds.

Status: active development. Core data layers, timeline, calendar/Health Connect integrations, and daily-mood are functional. Bank-notification capture and evening-recap features are in progress.

## Design Principles

Three product rules that shape both the UX and the data model:

1. **Passive over manual.** Data should arrive on its own — from the system calendar, Health Connect, and parsed bank notifications — rather than being typed in.
2. **No guilt.** No streaks, no red overspend warnings, no "you didn't fill this in." An empty day is simply a quiet day.
3. **Context, not control.** The app surfaces connections ("evening out ≈ $80 and 4 hours") but never issues instructions.

These principles are reflected in the code: nullable-by-design fields distinguish "not measured" from "measured zero"; sealed state hierarchies make invalid combinations unrepresentable; answer buttons are visually equal-weight so no choice is framed as "correct."

## Features

- Reactive timeline that merges expenses, calendar events, and health data via `combine()` into a single `StateFlow<HubState>`.
- System calendar read via `CalendarContract` (`Instances` + `Events`), with runtime permission flow and graceful degradation.
- Health Connect integration for steps, sleep, and exercise sessions.
- Daily mood with a five-emoji fan and per-day Canvas overlays (rain, sun glint) rendered via `withFrameNanos` with a reused particle pool.
- Expense entry (Room + Flow, hand-written migrations).
- Interval-based matching of expenses to calendar events (windowed overlap with configurable buffer).
- Passive bank-expense capture through `NotificationListenerService` and per-bank parser plugins that turn push notifications into draft transactions confirmed with one tap.

## Tech Stack

| Area | Choice |
|---|---|
| Language | Kotlin 2.2 |
| UI | Jetpack Compose (Material 3) |
| Async | Coroutines, Flow (`combine`, `flatMapLatest`, `stateIn`) |
| Persistence | Room 2.7 (hand-written migrations, `Flow` DAOs), DataStore |
| DI | Koin 3.5 |
| Health data | Health Connect 1.1 |
| Calendar | `CalendarContract` via `ContentResolver` |
| Background work | WorkManager |
| Navigation | Navigation Compose (type-safe, `@Serializable` routes) |
| Date/time | `kotlinx-datetime` |
| Testing | JUnit, `kotlinx-coroutines-test`, Turbine, Room `MigrationTestHelper` |
| Build | AGP 9.2, Gradle version catalog, KSP |

## Architecture

Multi-module with dependency direction `feature → domain ← data`. The domain layer is pure Kotlin, giving KMP-readiness and android-free unit tests for business logic.

**Key decisions:**

- **`:core:domain` is pure Kotlin** — zero android imports, enforced by module boundaries at compile time.
- **Single Source of Truth.** The UI only reads from Room via `Flow`. External sources (Health Connect, calendar) update the local store; the UI reacts through the reactive stream and never reads the network/provider directly.
- **Feature api / impl split.** Each feature exposes a small `:api` module (interfaces, navigation contracts) that other features and `:app` depend on, and a private `:impl` module that only `:app` wires in at runtime. This prevents circular feature dependencies and keeps compile-time boundaries between features.
- **Sealed state modeling.** External-source availability is modeled to make invalid states unrepresentable, e.g. `HealthState = NotInstalled | NoPermission(toRequest) | Available(data)`, where data lives *only* inside `Available`.
- **Idempotent background work.** `WorkManager` tasks check persisted status before side effects, so retries and reboots never double-fire.
- **Money as minor units (`Long`).** Currency is never `Double`.
- **Hand-written Room migrations** with `MigrationTestHelper` coverage. `fallbackToDestructiveMigration` is not used — user data is never discarded on schema change.

## Project Structure

```
:app                        // application entry point, DI wiring, navigation graph
:core:di                    // shared DI configuration
:core:domain                // pure Kotlin — entities, use cases, repository interfaces (zero android)
:core:data                  // Room, Health Connect, ContentResolver, DataStore
:core:presentation          // shared MVI / state primitives
:core:ui                    // Compose design tokens & shared components
:feature:expenses:api       // public contracts (nav routes, interfaces)
:feature:expenses:impl      // implementation (screens, view models, DI module)
:feature:hub:api
:feature:hub:impl
:feature:calendar:api
:feature:calendar:impl
```

## Getting Started

### Prerequisites

- JDK 17+
- Android Studio (recent stable) with AGP 9.2 support
- A physical device for testing Health Connect and bank-notification capture (emulators do not deliver these)

### Build

```bash
./gradlew assembleDebug
```

### Install on a connected device

```bash
./gradlew installDebug
```

## Testing

```bash
./gradlew test                        # all JVM unit tests
./gradlew :core:domain:test           # domain-only tests (fastest feedback loop)
./gradlew connectedDebugAndroidTest   # instrumented tests, including Room migrations
```

Business logic in `:core:domain` is covered by JUnit. Room migrations are covered by instrumented tests using `MigrationTestHelper` against exported schema JSONs.

## Roadmap

- [x] Multi-module architecture with api/impl feature split
- [x] Expense entry (Room + Flow)
- [x] System calendar integration (read-only, permission flow)
- [x] Heterogeneous timeline (expenses, events, health)
- [x] Health Connect (steps, sleep, workouts)
- [x] Daily mood with live-glass Canvas overlays
- [x] Expense ↔ event matching (interval overlap)
- [ ] Passive bank-notification expense capture
- [ ] "Cost of an evening" aggregate for linked expenses
- [ ] Evening recap (story-format day summary)
- [ ] Subscription detection
- [ ] Weekly correlations (on-device)

## Privacy

All data stays on-device. There is no backend, no account, and no telemetry. The local Room database is the only store.

## License

[MIT](LICENSE)
