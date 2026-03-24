param(
    [ValidateSet("all", "pass1", "pass2", "pass3")]
    [string]$Pass = "all",
    [string]$Repo = "onimurasame/VaniaGame",
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$gh = "C:\Program Files\GitHub CLI\gh.exe"

function Ensure-GhAuth {
    if ($DryRun) { return }
    & $gh auth status | Out-Null
}

function GhJson([string]$argsLine) {
    $cmd = "& `"$gh`" $argsLine"
    return Invoke-Expression $cmd
}

function RunGh([string]$argsLine) {
    if ($DryRun) {
        Write-Output "[DRY-RUN] gh $argsLine"
        return
    }
    $cmd = "& `"$gh`" $argsLine"
    Invoke-Expression $cmd | Out-Null
}

$epics = @(
    @{
        key = "E1"; title = "ONI-EPIC-01: Platform Modernization and Build Reliability"; priority = "priority:P1"; size = "size:3";
        body = @"
# ONI-EPIC-01: Platform Modernization and Build Reliability

## Status
Done (implemented on feature branch, pending merge)

## Epic Goal
Modernize build tooling to Java 21 and current Gradle/Android conventions so development and automation run on a supported stack.
"@
    },
    @{
        key = "E2"; title = "ONI-EPIC-02: MVP Core Gameplay and UX Skeleton"; priority = "priority:P1"; size = "size:3";
        body = @"
# ONI-EPIC-02: MVP Core Gameplay and UX Skeleton

## Status
Done (implemented on feature branch, pending merge)

## Epic Goal
Deliver a playable metroidvania MVP slice with menu, room traversal, HUD/status, combat placeholders, and progression gating.
"@
    },
    @{
        key = "E3"; title = "ONI-EPIC-03: Deterministic Simulation and Automated Verification"; priority = "priority:P1"; size = "size:3";
        body = @"
# ONI-EPIC-03: Deterministic Simulation and Automated Verification

## Status
Done (implemented on feature branch, pending merge)

## Epic Goal
Enable deterministic simulation, replay, and invariant validation to reduce manual regression testing.
"@
    },
    @{
        key = "E4"; title = "ONI-EPIC-04: Autonomous QA Reporting and CI Enforcement"; priority = "priority:P1"; size = "size:3";
        body = @"
# ONI-EPIC-04: Autonomous QA Reporting and CI Enforcement

## Status
Done (implemented on feature branch, pending merge)

## Epic Goal
Provide unattended QA execution and standardized reports locally and in CI.
"@
    },
    @{
        key = "E5"; title = "ONI-EPIC-05: Backlog Governance and Story Traceability"; priority = "priority:P1"; size = "size:3";
        body = @"
# ONI-EPIC-05: Backlog Governance and Story Traceability

## Status
Done (implemented on feature branch, pending merge)

## Epic Goal
Standardize ONI-based backlog, branching, and PR conventions.
"@
    }
)

$stories = @(
    @{ id = "ONI-101"; title = "ONI-101: Build with Java 21 and modern Gradle"; epic = "E1"; priority="priority:P0"; size="size:8"; dependsOn=@(); as="developer"; want="build the project using Java 21 and modern Gradle tooling"; so="develop and run automation on a supported stack"; technical="Wrapper/toolchain migration and Kotlin/JVM target alignment."},
    @{ id = "ONI-102"; title = "ONI-102: Assemble Android debug on modern stack"; epic = "E1"; priority="priority:P0"; size="size:8"; dependsOn=@("ONI-101"); as="mobile developer"; want="assemble Android debug artifacts on the migrated stack"; so="validate mobile builds in the same pipeline"; technical="AGP modernization, AndroidX, manifest compatibility."},
    @{ id = "ONI-201"; title = "ONI-201: Main menu with Start Game and Exit"; epic = "E2"; priority="priority:P1"; size="size:3"; dependsOn=@("ONI-101"); as="player"; want="navigate a main menu with Start Game and Exit"; so="enter gameplay or close cleanly"; technical="Dedicated menu screen and input handling."},
    @{ id = "ONI-202"; title = "ONI-202: Room traversal with platforming baseline"; epic = "E2"; priority="priority:P1"; size="size:8"; dependsOn=@("ONI-201"); as="player"; want="move, jump, and traverse connected rooms"; so="experience metroidvania traversal"; technical="Room transitions and baseline movement physics."},
    @{ id = "ONI-203"; title = "ONI-203: HUD bars and status/inventory overlay"; epic = "E2"; priority="priority:P1"; size="size:5"; dependsOn=@("ONI-202"); as="player"; want="see health bars and open status/inventory"; so="track combat and progression"; technical="HUD pass + status overlay data."},
    @{ id = "ONI-204"; title = "ONI-204: Enemy combat and progression gate"; epic = "E2"; priority="priority:P1"; size="size:8"; dependsOn=@("ONI-202"); as="player"; want="fight enemies and unlock gates via relic"; so="progress through blocked paths"; technical="Directional attacks, patrol AI, relic gate flags."},
    @{ id = "ONI-205"; title = "ONI-205: Solid collision integrity for platforms"; epic = "E2"; priority="priority:P1"; size="size:5"; dependsOn=@("ONI-202"); as="player"; want="have reliable collisions"; so="avoid geometry pass-through"; technical="Vertical crossing and horizontal solid resolution."},
    @{ id = "ONI-301"; title = "ONI-301: Deterministic headless game simulation"; epic = "E3"; priority="priority:P1"; size="size:8"; dependsOn=@("ONI-202","ONI-204","ONI-205"); as="QA engineer"; want="run deterministic headless simulation"; so="verify logic without rendering"; technical="Step-based deterministic simulation with snapshots."},
    @{ id = "ONI-302"; title = "ONI-302: Fuzzed invariant testing over input traces"; epic = "E3"; priority="priority:P1"; size="size:5"; dependsOn=@("ONI-301"); as="QA engineer"; want="fuzz traces with invariants"; so="detect unstable transitions"; technical="Seeded trace generation and invariant assertions."},
    @{ id = "ONI-303"; title = "ONI-303: Input trace recording and replay commands"; epic = "E3"; priority="priority:P1"; size="size:5"; dependsOn=@("ONI-301"); as="developer"; want="record and replay gameplay inputs"; so="convert bugs into regressions"; technical="Trace format, recorder, replay CLI/task."},
    @{ id = "ONI-304"; title = "ONI-304: Golden expectation validation for replay"; epic = "E3"; priority="priority:P1"; size="size:5"; dependsOn=@("ONI-303"); as="QA lead"; want="validate replay against golden outcomes"; so="detect drift strictly"; technical="Expectation format, generator, strict replay checks."},
    @{ id = "ONI-305"; title = "ONI-305: Batch replay and guarded golden regeneration"; epic = "E3"; priority="priority:P1"; size="size:3"; dependsOn=@("ONI-304"); as="developer"; want="batch replay traces and guard golden updates"; so="scale safely"; technical="Batch tasks and write-guard flag."},
    @{ id = "ONI-401"; title = "ONI-401: Local unattended QA report generation"; epic = "E4"; priority="priority:P1"; size="size:3"; dependsOn=@("ONI-305"); as="team member"; want="generate unattended QA reports"; so="share verification evidence"; technical="PowerShell + Bash report generation with pass/fail exit."},
    @{ id = "ONI-402"; title = "ONI-402: CI enforcement and QA artifact publication"; epic = "E4"; priority="priority:P1"; size="size:3"; dependsOn=@("ONI-401"); as="maintainer"; want="enforce QA in CI and publish report artifact"; so="block regressions from merging"; technical="Workflow gates and artifact upload."},
    @{ id = "ONI-501"; title = "ONI-501: ONI backlog/branch/PR traceability standard"; epic = "E5"; priority="priority:P2"; size="size:2"; dependsOn=@("ONI-EPIC-01","ONI-EPIC-02","ONI-EPIC-03","ONI-EPIC-04"); as="product and engineering team"; want="standardize ONI traceability"; so="link stories, branches, PRs, and QA evidence"; technical="Issue/PR templates, rules, and backlog conventions."}
)

Ensure-GhAuth

$existingIssuesJson = GhJson "issue list --repo $Repo --state all --limit 200 --json number,title,url"
$existingIssues = @{}
($existingIssuesJson | ConvertFrom-Json) | ForEach-Object { $existingIssues[$_.title] = $_.number }

function UpsertIssue([string]$title,[string]$body) {
    if ($existingIssues.ContainsKey($title)) {
        $num = $existingIssues[$title]
        $tmp = [System.IO.Path]::GetTempFileName()
        Set-Content -Path $tmp -Value $body -Encoding UTF8
        if ($DryRun) { Write-Output "[DRY-RUN] edit issue #$num ($title)" } else { & $gh issue edit $num --repo $Repo --body-file $tmp | Out-Null }
        Remove-Item $tmp -Force
        return $num
    }
    if ($DryRun) { Write-Output "[DRY-RUN] create issue ($title)"; return -1 }
    $url = & $gh issue create --repo $Repo --title $title --body $body
    $num = ($url.Trim().Split('/')[-1] -as [int])
    $existingIssues[$title] = $num
    return $num
}

$epicNumbers = @{}
if ($Pass -in @("all","pass1","pass2","pass3")) {
    foreach ($e in $epics) {
        $epicNumbers[$e.key] = UpsertIssue $e.title $e.body
    }
}
foreach ($e in $epics) {
    if (-not $epicNumbers.ContainsKey($e.key)) {
        $epicNumbers[$e.key] = $existingIssues[$e.title]
    }
}

if ($Pass -in @("all","pass1")) {
    foreach ($s in $stories) {
        $epicNum = $epicNumbers[$s.epic]
        $depText = if ($s.dependsOn.Count -gt 0) { ($s.dependsOn -join ", ") } else { "none" }
        $body = @"
As a $($s.as)
I want to $($s.want)
So that I can $($s.so)

Status: Done (implemented on feature branch, pending merge)
Epic: #$epicNum

Acceptance Criteria:
Given the implemented feature area is exercised
When the corresponding flow is executed
Then expected behavior occurs
And automated QA gates remain green for this scope

Technical Details
$($s.technical)

Dependencies
$depText
"@
        UpsertIssue $s.title $body | Out-Null
    }
}

$milestoneTitle = "MVP Session Delivery (Pending Merge)"
if ($Pass -in @("all","pass2")) {
    $milestones = (GhJson "api repos/$Repo/milestones") | ConvertFrom-Json
    $existingMilestone = $milestones | Where-Object { $_.title -eq $milestoneTitle } | Select-Object -First 1
    if (-not $existingMilestone) {
        RunGh "api repos/$Repo/milestones -f title=""$milestoneTitle"" -f state=""open"" -f description=""Stories implemented in feature branch and awaiting merge."""
    }

    foreach ($e in $epics) {
        $num = $existingIssues[$e.title]
        RunGh "issue edit $num --repo $Repo --milestone ""$milestoneTitle"" --add-label ""type:epic,status:implemented-pending-merge,$($e.priority),$($e.size)"""
    }
    foreach ($s in $stories) {
        $num = $existingIssues[$s.title]
        RunGh "issue edit $num --repo $Repo --milestone ""$milestoneTitle"" --add-label ""type:story,status:implemented-pending-merge,$($s.priority),$($s.size)"""
    }
}

if ($Pass -in @("all","pass3")) {
    $branch = if ($DryRun) { "feature/unknown" } else { (& git rev-parse --abbrev-ref HEAD).Trim() }
    $commit = if ($DryRun) { "unknown" } else { (& git rev-parse --short HEAD).Trim() }

    foreach ($s in $stories) {
        $num = $existingIssues[$s.title]
        $deps = if ($s.dependsOn.Count -gt 0) { $s.dependsOn -join ", " } else { "none" }
        $comment = @"
Third-pass dependency/context update:
- Depends on: $deps
- Branch: $branch
- Commit: $commit
- Status reference: implemented on feature branch, pending merge
"@
        RunGh "issue comment $num --repo $Repo --body ""$comment"""
    }

    foreach ($e in $epics) {
        $epicNum = $existingIssues[$e.title]
        $linkedStories = $stories | Where-Object { $_.epic -eq $e.key } | ForEach-Object { "#$($existingIssues[$_.title])" }
        $storyText = $linkedStories -join ", "
        RunGh "issue comment $epicNum --repo $Repo --body ""Epic story links: $storyText"""
    }
}

Write-Output "ONI backlog orchestration completed. Pass mode: $Pass"
