param(
    [Parameter(Mandatory = $true)][int]$IssueNumber,
    [string]$StatusLabel = "status:implemented-pending-merge",
    [string]$Milestone = "MVP Session Delivery (Pending Merge)",
    [string]$PriorityLabel,
    [string]$SizeLabel,
    [string]$DependsOn = "",
    [string]$Evidence = ":core:test, :desktop:compileKotlin, :android:assembleDebug, :core:replayTraceBatch, qa-report",
    [string]$Repo = "onimurasame/VaniaGame"
)

$ErrorActionPreference = "Stop"
$gh = "C:\Program Files\GitHub CLI\gh.exe"

function Ensure-GhAuth {
    & $gh auth status | Out-Null
}

function Join-NonEmpty([string[]]$items) {
    return ($items | Where-Object { $_ -and $_.Trim().Length -gt 0 }) -join ","
}

Ensure-GhAuth

$labels = Join-NonEmpty @($StatusLabel, $PriorityLabel, $SizeLabel)
if ($labels.Length -gt 0) {
    & $gh issue edit $IssueNumber --repo $Repo --add-label $labels | Out-Null
}
if ($Milestone -and $Milestone.Trim().Length -gt 0) {
    & $gh issue edit $IssueNumber --repo $Repo --milestone $Milestone | Out-Null
}

$branch = (& git rev-parse --abbrev-ref HEAD).Trim()
$commit = (& git rev-parse --short HEAD).Trim()
$depText = if ($DependsOn.Trim().Length -gt 0) { "Depends on: $DependsOn" } else { "Depends on: none explicitly declared" }

$comment = @"
Third-pass automation update:
- Status label applied: $StatusLabel
- Milestone: $Milestone
- Branch: $branch
- Commit: $commit
- $depText
- Verification evidence: $Evidence
"@

& $gh issue comment $IssueNumber --repo $Repo --body $comment | Out-Null
Write-Output "Updated issue #$IssueNumber with status/milestone/context."
