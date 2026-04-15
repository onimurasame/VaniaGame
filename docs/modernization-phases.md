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

## Phase 2 - LWJGL3 desktop backend migration

Goal: move desktop runtime off deprecated LWJGL2 backend before broader Gradle/Kotlin upgrades.

Changes:

- `build.gradle`
  - Desktop backend dependency switched from `gdx-backend-lwjgl` to `gdx-backend-lwjgl3`.
- `desktop/src/com/onimurasame/vania/desktop/DesktopLauncher.kt`
  - Migrated launcher API from LWJGL2 to LWJGL3 classes/config methods.
  - Added explicit title + vsync configuration.

Expected smoke test:

```bash
bash ./gradlew -PdesktopOnly=true :desktop:run
```

## Phase 3 - Gradle/Kotlin desktop-core upgrade

Goal: modernize desktop/core build pipeline to a current Gradle generation while Android remains decoupled.

Changes:

- `gradle/wrapper/gradle-wrapper.properties`
  - Wrapper upgraded from Gradle `4.6` to `8.7`.
- `build.gradle`
  - Kotlin Gradle plugin upgraded to `1.9.24`.
  - Android Gradle plugin classpath resolution skipped when running `-PdesktopOnly=true`.
- `desktop/build.gradle`
  - Migrated JavaExec tasks to modern `mainClass.set(...)`.
  - Migrated `dist` task away from removed `configurations.compile`.
  - Set Java compatibility to 1.8 for legacy libGDX compatibility.

Expected smoke test:

```bash
bash ./gradlew -PdesktopOnly=true :core:compileKotlin :desktop:compileKotlin
```

## Phase 4 - JDK toolchain modernization

Goal: remove manual JDK switching and make desktop/core builds deterministic on JDK 21.

Changes:

- `build.gradle`
  - Added Gradle Java toolchain configuration for language level 21 in Java projects.
  - Added Kotlin JVM toolchain configuration (`jvmToolchain(21)`) for Kotlin JVM projects.
- `gradle.properties`
  - Added `org.gradle.java.installations.paths` pointing to Homebrew `openjdk@21` installation.

Expected smoke test:

```bash
bash ./gradlew -PdesktopOnly=true :core:compileKotlin :desktop:compileKotlin
```

## Phase 5 - Steam packaging hardening

Goal: stabilize local packaging/deployment workflow for Steam Deck and Linux desktop test loops.

Changes:

- `scripts/package-desktop.sh`
  - Uses desktop-only Gradle mode (`-PdesktopOnly=true :desktop:dist`) for faster and safer packaging.
- `scripts/deploy-steamdeck.sh`
  - Deploys packaged build to Deck over SSH + rsync.
- `docs/steamdeck-deployment.md`
  - Updated to reflect current Gradle/Kotlin/JDK21 stack and deployment commands.

Expected smoke test:

```bash
scripts/package-desktop.sh --help
scripts/deploy-steamdeck.sh --help
```

## Phase 6 - Android reintegration

Goal: restore Android module compatibility after Gradle/Kotlin modernization so Android is no longer blocked by legacy AGP configuration.

Changes:

- `build.gradle`
  - Android Gradle Plugin upgraded to `8.5.2`.
  - Android module plugin ids modernized to `com.android.application` and `org.jetbrains.kotlin.android`.
  - Multidex dependency moved to AndroidX (`androidx.multidex:multidex:2.0.1`).
- `android/build.gradle`
  - Added `namespace`, migrated SDK config (`compileSdk`, `minSdk`, `targetSdk`) to current DSL.
  - Updated packaging options to new AGP resources syntax.
  - Updated legacy `compile` configuration references to `implementation` in IDE helper blocks.
- `gradle.properties`
  - Enabled AndroidX + Jetifier for Android dependency compatibility.

Expected validation:

```bash
bash ./gradlew :android:tasks
```

## Current phase status

- Phase 0 through Phase 6 completed and tagged.
