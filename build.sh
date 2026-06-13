#!/bin/bash
# Build debug APK

set -euo pipefail

WORKDIR="$(cd "$(dirname "$0")" && pwd)"
cd "$WORKDIR"

ensure_cmd() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Required command not found: $cmd"
    exit 1
  fi
}

# 1. Build the APK
echo "Building APK..."

BUILD_CMD=("./gradlew" "assembleDebug")

# Recover from missing wrapper JAR by generating wrapper with system Gradle.
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "gradle-wrapper.jar is missing. Attempting to generate it using system Gradle..."
  ensure_cmd gradle
  gradle wrapper
fi

"${BUILD_CMD[@]}"

# 2. Verify APK was created
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
  echo "Error: APK not found at $APK_PATH"
  exit 1
fi

echo "✓ APK successfully built at $APK_PATH"
