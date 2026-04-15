# Movement Feel Reference (SotN-Inspired)

This project now includes local reference sources under `references/`:

- `references/sotn-decomp` (direct SotN behavior reference)
- `references/openmv` (compact C movement loop reference)
- `references/Ultimate-Platformer-Controller-2D` (modern feel helpers: coyote/buffer/variable jump)

## 1) High-value reference snippets

### SotN decomp (`references/sotn-decomp`)

- `src/ric/pl_setstep.c`
  - `RicSetWalk()` uses horizontal speed `FIX(1.25)`
  - `RicSetRun()` uses horizontal speed `FIX(2.25)`
  - `RicSetJump()` sets initial vertical velocity around `FIX(-5.4375)` (non-prologue)
  - `RicSetSlide()` / `RicSetSlideKick()` use speed `FIX(5.5)`
  - `RicSetFall()` / jump state transitions preserve or zero horizontal momentum depending on previous state
- `src/dra/pads.c`
  - Separate `pressed`, `tapped`, and `repeat` input channels with configurable repeat timers.

Why this matters: SotN gets its feel from strict state-specific speeds and deterministic transitions more than from continuous analog-like acceleration.

### openmv (`references/openmv`)

- `logic/src/player.c`
  - Explicit constants:
    - `move_speed = 300`
    - `jump_force = -800`
    - `gravity = g_gravity`
    - `max_gravity = g_max_gravity`
    - `accel = 2000`
    - `friction = 800`
    - `dash_force = 1000`
    - `max_dash = 0.15`
    - `dash_cooldown = 0.3`
    - `low_jump_mul = 3000.0f`
  - Movement loop pattern:
    - apply gravity
    - clamp terminal velocity
    - apply accel/friction
    - integrate position
    - resolve collisions
    - then evaluate grounded and jump logic
  - Variable jump:
    - if jump is released while rising, add extra downward force.

Why this matters: this is a simple, practical implementation order for stable movement physics.

### Ultimate Platformer Controller (`references/Ultimate-Platformer-Controller-2D`)

- `UltimatePlatformerController.gd`
  - `coyoteTime = 0.2`
  - `jumpBuffering = 0.2`
  - `descendingGravityFactor = 1.3`
  - `jumpVariable = 2`
  - computes:
    - `acceleration = maxSpeed / timeToReachMaxSpeed`
    - `deceleration = -maxSpeed / timeToReachZeroSpeed`
  - helpers:
    - `_coyoteTime()`
    - `_bufferJump()`
    - `_jump()`

Why this matters: these feel helpers eliminate frustrating missed jumps and produce tighter control without changing your art/animations.

## 2) Mapping to current VaniaGame code

Current state:

- `core/src/com/onimurasame/vania/controller/PlayerController.kt` only wires player + input.
- `core/src/com/onimurasame/vania/controller/PlayerInputController.kt` currently sets animation states from key events.
- `core/src/com/onimurasame/vania/entity/Player.kt` has `x`, `y`, and high-level states, but no velocity/physics fields.
- `core/src/com/onimurasame/vania/screen/Gameplay.kt` does not run a simulation update before render.

To implement SotN-like feel, add a deterministic movement step per frame and keep input/state transitions separated from physics integration.

## 3) First-pass tuning set (recommended)

Use these as starting values, then tune by feel:

- Horizontal
  - `walkSpeed = 1.25f` (SotN reference ratio)
  - `runSpeed = 2.25f`
  - `groundAcceleration = 25f`
  - `groundDeceleration = 35f`
- Vertical
  - `jumpVelocity = -5.4f`
  - `gravityUp = 22f`
  - `gravityDown = 30f` (about `1.35x` upward gravity)
  - `terminalVelocity = 12f`
  - `jumpReleaseMultiplier = 0.45f` (short-hop behavior)
- Input forgiveness
  - `coyoteTimeSec = 0.10f`
  - `jumpBufferSec = 0.10f`

Notes:

- Keep the SotN walk/run ratio (`2.25 / 1.25 = 1.8`) even if absolute speeds are retuned.
- If movement feels floaty, increase `gravityDown` first, not base walk speed.
- If jump feels too snappy, reduce `jumpReleaseMultiplier` impact (move toward `0.6f`).

## 4) Implementation order for this repo

1. Add movement model fields to `Player`:
   - `velocityX`, `velocityY`
   - grounded flags and timers (`isGrounded`, `coyoteTimer`, `jumpBufferTimer`)
   - intent flags from input (`moveAxis`, `jumpPressedThisFrame`, `jumpHeld`)
2. Extend `PlayerController` with `update(delta: Float)`:
   - consume input intents
   - apply jump buffer/coyote checks
   - apply horizontal accel/decel
   - apply gravity (up vs down)
   - clamp terminal velocity
   - integrate position
   - clamp to floor / resolve collisions
3. Call `playerController.update(delta)` from `Gameplay.render(delta)` before renderer calls.
4. Keep `PlayerRenderer` state-driven for animation only.

## 5) Feel goals to validate

- Standing jump should be short and controllable.
- Running jump should preserve enough forward momentum.
- Late jump presses near edges still trigger (coyote).
- Early jump presses just before landing still trigger (buffer).
- Releasing jump early should produce a clear short-hop.

If these pass, the controller is already close to the expected metroidvania feel baseline.
