#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_PACKAGE_DIR="${ROOT_DIR}/build/steamdeck/VaniaGame"

usage() {
  cat <<'EOF'
Build + deploy VaniaGame package to a Steam Deck over SSH.

Usage:
  scripts/deploy-steamdeck.sh --host <deck-ip-or-hostname> [options]

Required:
  --host HOST              Steam Deck host/IP

Optional:
  --user USER              SSH username (default: deck)
  --target-dir PATH        Remote install path (default: /home/deck/Games/VaniaGame)
  --skip-build             Do not run local package step
  --bundle-jre PATH        Forwarded to scripts/package-desktop.sh --bundle-jre
  --help                   Show this message

Examples:
  scripts/deploy-steamdeck.sh --host 192.168.1.42
  scripts/deploy-steamdeck.sh --host steamdeck.local --bundle-jre "/Users/me/jre-17"
EOF
}

DECK_HOST=""
DECK_USER="deck"
DECK_TARGET_DIR="/home/deck/Games/VaniaGame"
SKIP_BUILD="false"
BUNDLE_JRE_PATH=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --host)
      shift
      DECK_HOST="${1:-}"
      ;;
    --user)
      shift
      DECK_USER="${1:-}"
      ;;
    --target-dir)
      shift
      DECK_TARGET_DIR="${1:-}"
      ;;
    --skip-build)
      SKIP_BUILD="true"
      ;;
    --bundle-jre)
      shift
      BUNDLE_JRE_PATH="${1:-}"
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown arg: $1" >&2
      usage
      exit 1
      ;;
  esac
  shift
done

if [[ -z "${DECK_HOST}" ]]; then
  echo "--host is required" >&2
  usage
  exit 1
fi

if [[ "${SKIP_BUILD}" != "true" ]]; then
  if [[ -n "${BUNDLE_JRE_PATH}" ]]; then
    "${ROOT_DIR}/scripts/package-desktop.sh" --bundle-jre "${BUNDLE_JRE_PATH}"
  else
    "${ROOT_DIR}/scripts/package-desktop.sh"
  fi
fi

if [[ ! -d "${LOCAL_PACKAGE_DIR}" ]]; then
  echo "Package not found at ${LOCAL_PACKAGE_DIR}. Run package step first." >&2
  exit 1
fi

echo "==> Creating remote directory"
ssh "${DECK_USER}@${DECK_HOST}" "mkdir -p \"${DECK_TARGET_DIR}\""

echo "==> Syncing package to Steam Deck"
rsync -az --delete "${LOCAL_PACKAGE_DIR}/" "${DECK_USER}@${DECK_HOST}:${DECK_TARGET_DIR}/"

echo "==> Ensuring launcher is executable"
ssh "${DECK_USER}@${DECK_HOST}" "chmod +x \"${DECK_TARGET_DIR}/VaniaGame.sh\""

cat <<EOF
Deployment complete.

Run remotely:
  ssh ${DECK_USER}@${DECK_HOST} "cd \"${DECK_TARGET_DIR}\" && ./VaniaGame.sh"

Add to Steam as Non-Steam Game on Deck:
  ${DECK_TARGET_DIR}/VaniaGame.sh
EOF
