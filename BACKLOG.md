# Backlog Conventions

Use `ONI-<number>` as the single story identifier across backlog, branches, commits, and PRs.

## Story Lifecycle

1. Create issue with title starting `ONI-<number>`.
2. Create branch from `master`:
   - `feature/oni-<number>-short-slug`
3. Include ONI ID in commit message first line.
4. Open PR using template and link issue (`Closes #...`).
5. Attach QA evidence (`build/reports/qa/latest.md`).

## Branch Naming

- Feature: `feature/oni-<number>-<slug>`
- Bugfix: `bugfix/oni-<number>-<slug>`
- Chore: `chore/oni-<number>-<slug>`

## Minimal Done Criteria

- `:core:test` passes
- replay/golden checks pass
- `AUTONOMOUS_QA.md` workflow remains valid
- CI QA workflow passes
