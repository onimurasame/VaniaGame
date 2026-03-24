# Autonomous QA Operations (Project-Only)

This project is configured for unattended agent workflows and deterministic gameplay verification.

## Project Rule

- Persistent project rule: `.cursor/rules/autonomous-mvp-workflow.mdc`
- Scope: this repository only.
- Policy: continue autonomously until gates pass, escalate only for destructive/credentialed actions.

## Gameplay Trace Workflow

### 1) Record trace in-game

- Launch game: `./gradlew :desktop:run`
- Press `F9` to start recording.
- Play scenario.
- Press `F9` again to stop and save trace.
- Output trace: `build/traces/latest.trace`

### 2) Replay trace with invariant checks

```bash
./gradlew :core:replayTrace -PtraceFile=build/traces/latest.trace
```

### 3) Generate golden expectation from a trace

```bash
./gradlew :core:generateGoldenExpectation -PtraceFile=build/traces/latest.trace -PoutputFile=build/traces/latest.expect
```

### 4) Replay trace against strict expectation

```bash
./gradlew :core:replayTrace -PtraceFile=build/traces/latest.trace -PexpectationFile=build/traces/latest.expect
```

### 5) Batch replay all repository traces

```bash
./gradlew :core:replayTraceBatch -PtracesDir=traces
```

### 6) Regenerate all golden expectations (guarded)

```bash
./gradlew :core:updateGoldenExpectations -PtracesDir=traces -PallowGoldenWrite=true
```

## Verification Gates

- Unit + simulation tests:

```bash
./gradlew :core:test
```

- Desktop compile:

```bash
./gradlew :desktop:compileKotlin
```

- Android debug build (if Android touched):

```bash
./gradlew :android:assembleDebug
```

## CI

- GitHub Actions workflow: `.github/workflows/qa-automation.yml`
- Runs:
  - `:core:test`
  - `:desktop:compileKotlin`
  - `:core:replayTraceBatch -PtracesDir=traces`

## Report Mechanism

Generate a single QA report file (Windows PowerShell):

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\qa-report.ps1
```

Optional trace directory override:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\qa-report.ps1 -TraceDir traces
```

Outputs:

- `build/reports/qa/qa-report-<timestamp>.md`
- `build/reports/qa/latest.md`

Report run exits non-zero if any gate fails, so it is CI-friendly.

Linux/macOS:

```bash
bash ./tools/qa-report.sh traces
```

CI also uploads the latest report as a workflow artifact (`qa-report`).

## Backlog third pass

After implementation and QA gates, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\oni-backlog-orchestrator.ps1 -Pass all
```

Process reference:

- `BACKLOG_AUTOMATION_PROCESS.md`
