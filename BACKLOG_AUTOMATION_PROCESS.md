# ONI Backlog Automation Process

Use this process after implementing new work so GitHub issues always reflect reality.

## When to run

- After implementation is complete on a feature branch.
- After verification gates pass.
- Before or at PR creation.

## Verification gates

- `./gradlew :core:test`
- `./gradlew :desktop:compileKotlin`
- `./gradlew :core:replayTraceBatch -PtracesDir=traces`
- `./gradlew :android:assembleDebug` (when Android is touched)
- `powershell -ExecutionPolicy Bypass -File .\tools\qa-report.ps1`

## Single-command orchestration (passes 1/2/3)

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\oni-backlog-orchestrator.ps1 -Pass all
```

This command automatically:

- Pass 1: create/update epic and story issue bodies from canonical ONI definitions.
- Pass 2: apply status/priority/size labels and milestone.
- Pass 3: add dependency/context comments and epic-story link comments.

### Optional targeted pass

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\oni-backlog-orchestrator.ps1 -Pass pass2
```

### Dry run

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\oni-backlog-orchestrator.ps1 -Pass all -DryRun
```

## Dependency policy

- Add dependency note for all stories that are not standalone.
- Use issue references in comments/body (`#123` format).
- Keep dependencies directional (story depends on prerequisite story).

## Merge status transition

After merge to default branch:

1. Replace label `status:implemented-pending-merge` with `status:merged` (create this label if missing).
2. Add a merge confirmation comment referencing merge commit or PR URL.
3. Close story issue if done criteria are fully met.

## Recommended cadence

- Story created -> template fill
- Implementation done -> third-pass update
- PR opened -> evidence checklist completed
- Merge done -> status transition + close issue
