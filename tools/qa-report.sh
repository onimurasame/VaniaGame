#!/usr/bin/env bash
set -euo pipefail

TRACE_DIR="${1:-traces}"

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
REPORT_DIR="$REPO_ROOT/build/reports/qa"
mkdir -p "$REPORT_DIR"

TIMESTAMP="$(date +"%Y-%m-%d_%H-%M-%S")"
REPORT_PATH="$REPORT_DIR/qa-report-$TIMESTAMP.md"
LATEST_PATH="$REPORT_DIR/latest.md"

declare -a STEP_NAMES=(
  "Core tests"
  "Desktop compile"
  "Android debug build"
  "Trace replay batch"
)
declare -a STEP_CMDS=(
  "./gradlew :core:test"
  "./gradlew :desktop:compileKotlin"
  "./gradlew :android:assembleDebug"
  "./gradlew :core:replayTraceBatch -PtracesDir=$TRACE_DIR"
)

declare -a STEP_STATUS=()
declare -a STEP_DURATION=()

ALL_PASSED=true

cd "$REPO_ROOT"
for i in "${!STEP_CMDS[@]}"; do
  START="$(date +%s)"
  if eval "${STEP_CMDS[$i]}" >/dev/null; then
    STATUS="PASS"
  else
    STATUS="FAIL"
    ALL_PASSED=false
  fi
  END="$(date +%s)"
  STEP_STATUS+=("$STATUS")
  STEP_DURATION+=("$((END-START))")
done

OVERALL="PASS"
if [[ "$ALL_PASSED" != true ]]; then
  OVERALL="FAIL"
fi

{
  echo "# QA Verification Report"
  echo
  echo "- Date: $(date -Iseconds)"
  echo "- Overall: **$OVERALL**"
  echo "- Trace dir: $TRACE_DIR"
  echo
  echo "## Steps"
  echo
  echo "| Step | Status | Duration (s) | Command |"
  echo "|---|---|---:|---|"
  for i in "${!STEP_NAMES[@]}"; do
    echo "| ${STEP_NAMES[$i]} | ${STEP_STATUS[$i]} | ${STEP_DURATION[$i]} | ${STEP_CMDS[$i]} |"
  done
} > "$REPORT_PATH"

cp "$REPORT_PATH" "$LATEST_PATH"

echo "QA report written: $REPORT_PATH"
echo "Latest report: $LATEST_PATH"

if [[ "$ALL_PASSED" != true ]]; then
  exit 1
fi
