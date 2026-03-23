param(
    [string]$TraceDir = "traces",
    [string]$JavaHome = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$reportDir = Join-Path $repoRoot "build\reports\qa"
New-Item -ItemType Directory -Path $reportDir -Force | Out-Null

$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$reportPath = Join-Path $reportDir "qa-report-$timestamp.md"
$latestPath = Join-Path $reportDir "latest.md"

$env:JAVA_HOME = $JavaHome
$pathEntries = $env:Path -split ';' | Where-Object {
    $_ -notmatch 'Eclipse Adoptium\\jdk-8' -and $_ -notmatch 'Eclipse Adoptium\\jdk-21'
}
$env:Path = "$env:JAVA_HOME\bin;" + ($pathEntries -join ';')

$steps = @(
    @{ Name = "Core tests"; Cmd = "./gradlew :core:test" },
    @{ Name = "Desktop compile"; Cmd = "./gradlew :desktop:compileKotlin" },
    @{ Name = "Android debug build"; Cmd = "./gradlew :android:assembleDebug" },
    @{ Name = "Trace replay batch"; Cmd = "./gradlew :core:replayTraceBatch ""-PtracesDir=$TraceDir""" }
)

$results = @()
$allPassed = $true

Push-Location $repoRoot
try {
    foreach ($step in $steps) {
        $start = Get-Date
        try {
            Invoke-Expression $step.Cmd | Out-Null
            $exitCode = $LASTEXITCODE
            if ($exitCode -ne 0) {
                throw "Command exited with code $exitCode"
            }
            $status = "PASS"
        } catch {
            $status = "FAIL"
            $allPassed = $false
        }
        $duration = [math]::Round(((Get-Date) - $start).TotalSeconds, 2)
        $results += [pscustomobject]@{
            Name = $step.Name
            Command = $step.Cmd
            Status = $status
            DurationSeconds = $duration
        }
    }
} finally {
    Pop-Location
}

$overall = if ($allPassed) { "PASS" } else { "FAIL" }
$dateIso = (Get-Date).ToString("s")

$content = @()
$content += "# QA Verification Report"
$content += ""
$content += "- Date: $dateIso"
$content += "- Overall: **$overall**"
$content += "- Java: $JavaHome"
$content += "- Trace dir: $TraceDir"
$content += ""
$content += "## Steps"
$content += ""
$content += "| Step | Status | Duration (s) | Command |"
$content += "|---|---|---:|---|"
foreach ($r in $results) {
    $content += "| $($r.Name) | $($r.Status) | $($r.DurationSeconds) | $($r.Command) |"
}

$content -join "`r`n" | Out-File -FilePath $reportPath -Encoding utf8 -Force
Copy-Item -Path $reportPath -Destination $latestPath -Force

Write-Output "QA report written: $reportPath"
Write-Output "Latest report: $latestPath"

if (-not $allPassed) {
    exit 1
}
