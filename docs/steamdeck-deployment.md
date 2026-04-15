# Steam Deck + PC Deployment (Kotlin/libGDX)

This project can target Steam Deck and Steam PC first by shipping a Linux desktop build and testing it directly on a Deck.

## Current project constraints

- Uses Gradle wrapper `8.7` and Kotlin `1.9.24` for desktop/core pipeline.
- Build runtime is pinned via `gradle.properties` to Homebrew `openjdk@21` path.
- Runtime on Deck can use either:
  - system `java`, or
  - a bundled Linux JRE (`runtime/bin/java`) copied with the game.

## Scripts added

- `scripts/package-desktop.sh`
  - Builds `-PdesktopOnly=true :desktop:dist`
  - Creates package at `build/steamdeck/VaniaGame`
  - Produces:
    - `vaniaGame.jar`
    - `VaniaGame.sh` launcher
  - Optional: `--bundle-jre /absolute/path/to/linux-jre`

- `scripts/deploy-steamdeck.sh`
  - Builds package (unless `--skip-build`)
  - Syncs package to Deck over SSH (`rsync`)
  - Makes launcher executable on Deck

## 1) One-time Steam Deck setup

1. On Deck, enable Developer Mode and SSH server.
2. Set a password for user `deck` (if not already set).
3. From your dev machine, verify:
   - `ssh deck@<deck-ip>`
   - `rsync` is available locally.

## 2) Build package locally

```bash
scripts/package-desktop.sh
```

If Deck does not have Java installed, bundle a Linux JRE:

```bash
scripts/package-desktop.sh --bundle-jre /absolute/path/to/linux-jre
```

## 3) Deploy to Steam Deck

```bash
scripts/deploy-steamdeck.sh --host <deck-ip>
```

Example:

```bash
scripts/deploy-steamdeck.sh --host steamdeck.local --bundle-jre /Users/you/jre-17-linux
```

Default install location on Deck:

- `/home/deck/Games/VaniaGame`

## 4) Run on Deck

SSH run:

```bash
ssh deck@<deck-ip> 'cd /home/deck/Games/VaniaGame && ./VaniaGame.sh'
```

Or in Gaming Mode:

1. Open Steam (Desktop mode) -> **Add a Non-Steam Game**
2. Point to: `/home/deck/Games/VaniaGame/VaniaGame.sh`
3. Return to Gaming Mode and launch.

## 5) Steam PC (Linux) test

Same package can run on Linux PC:

```bash
cd build/steamdeck/VaniaGame
./VaniaGame.sh
```

## 6) Notes for Steam release readiness

- For Steam distribution, plan to move from raw jar launcher to a proper packaged app per platform.
- Keep this Deck pipeline for rapid controller/performance testing.
- Next practical step: add CI artifacts for Linux desktop package and later add Windows packaging.
