# Build Modernization Phases

This document tracks the staged upgrade plan for modernizing the build while keeping Steam PC + Steam Deck delivery unblocked.

## Phase 0 - Baseline safety point

- Create modernization branch
- Tag pre-migration baseline
- Push branch/tag for rollback

Tag: `phase-0-baseline`

## Phase 1 - Desktop/Core decoupling from Android

Goal: make desktop/core iteration possible without loading Android project configuration.

Changes:

- `settings.gradle`
  - Added `-PdesktopOnly=true` switch to include only `desktop` and `core`.
- `build.gradle`
  - Guarded Android project block with `if (findProject(":android") != null)` so desktop-only settings do not fail configuration.
- `.gitignore`
  - Ignored local `references/` mirrors to keep commits focused.

Usage:

```bash
bash ./gradlew -PdesktopOnly=true :core:tasks :desktop:tasks
```

## Upcoming phases

- Phase 2: LWJGL3 desktop backend migration
- Phase 3: Gradle + Kotlin staged upgrades
- Phase 4: JDK toolchain modernization
- Phase 5: Steam packaging hardening
- Phase 6: Android reintegration
