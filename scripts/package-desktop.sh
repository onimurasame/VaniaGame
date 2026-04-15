#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST_DIR="${ROOT_DIR}/build/steamdeck/VaniaGame"
DESKTOP_LIBS_DIR="${ROOT_DIR}/desktop/build/libs"

usage() {
  cat <<'EOF'
Package a runnable Linux build for Steam Deck / Linux PC.

Usage:
  scripts/package-desktop.sh [--bundle-jre /absolute/path/to/jre]

Options:
  --bundle-jre PATH   Copy a Linux JRE into the package as ./runtime
                      (launcher uses runtime/bin/java automatically)

Environment:
  JAVA_HOME           Optional override (build defaults to pinned JDK 21 via gradle.properties)
EOF
}

BUNDLE_JRE_PATH=""
while [[ $# -gt 0 ]]; do
  case "$1" in
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

if [[ -n "${BUNDLE_JRE_PATH}" && ! -d "${BUNDLE_JRE_PATH}" ]]; then
  echo "Bundled JRE path does not exist: ${BUNDLE_JRE_PATH}" >&2
  exit 1
fi

echo "==> Building desktop dist jar"
(
  cd "${ROOT_DIR}"
  bash ./gradlew -PdesktopOnly=true :desktop:dist
)

if [[ ! -d "${DESKTOP_LIBS_DIR}" ]]; then
  echo "Desktop libs dir not found: ${DESKTOP_LIBS_DIR}" >&2
  exit 1
fi

DIST_JAR="$(ls -1t "${DESKTOP_LIBS_DIR}"/*.jar | head -n 1)"
if [[ -z "${DIST_JAR}" ]]; then
  echo "No dist jar found in ${DESKTOP_LIBS_DIR}" >&2
  exit 1
fi

echo "==> Preparing package at ${DIST_DIR}"
rm -rf "${DIST_DIR}"
mkdir -p "${DIST_DIR}"

cp "${DIST_JAR}" "${DIST_DIR}/vaniaGame.jar"

if [[ -n "${BUNDLE_JRE_PATH}" ]]; then
  echo "==> Bundling runtime from ${BUNDLE_JRE_PATH}"
  cp -R "${BUNDLE_JRE_PATH}" "${DIST_DIR}/runtime"
fi

cat > "${DIST_DIR}/VaniaGame.sh" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

if [[ -x "${SCRIPT_DIR}/runtime/bin/java" ]]; then
  JAVA_BIN="${SCRIPT_DIR}/runtime/bin/java"
else
  JAVA_BIN="${JAVA_BIN:-java}"
fi

exec "${JAVA_BIN}" -jar "${SCRIPT_DIR}/vaniaGame.jar"
EOF

chmod +x "${DIST_DIR}/VaniaGame.sh"

echo "==> Package ready"
echo "Path: ${DIST_DIR}"
echo "Launcher: ${DIST_DIR}/VaniaGame.sh"
