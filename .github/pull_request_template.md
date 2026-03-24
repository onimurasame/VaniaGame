## ONI Story

- ONI ID: `ONI-<number>`
- Issue link: `Closes #<issue-number>`
- Branch: `feature/oni-<number>-<slug>`

## What Changed

- 

## Verification Evidence

- [ ] `./gradlew :core:test`
- [ ] `./gradlew :desktop:compileKotlin`
- [ ] `./gradlew :core:replayTraceBatch -PtracesDir=traces`
- [ ] `powershell -ExecutionPolicy Bypass -File .\tools\qa-report.ps1`
- Report path: `build/reports/qa/latest.md`

## Replay/Golden Updates

- [ ] No trace updates needed
- [ ] Updated trace(s): `traces/...`
- [ ] Updated expectation(s): `traces/...`
- [ ] Used guarded update command (`-PallowGoldenWrite=true`) when regenerating

## Risks / Follow-ups

- 
