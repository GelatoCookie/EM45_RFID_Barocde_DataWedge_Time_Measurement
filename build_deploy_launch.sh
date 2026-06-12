#!/bin/bash
# Automated script to build, deploy, launch, and execute the app on a connected device.

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

usage() {
  cat <<'EOF'
Usage:
  ./build_deploy_launch.sh [--connected-tests]

Options:
  --connected-tests   Run connectedDebugAndroidTest after install/launch.
EOF
}

find_online_device() {
  adb start-server >/dev/null
  adb devices | awk 'NR>1 && $2=="device" {print $1; exit}'
}

require_online_device() {
  local device
  device="$(find_online_device)"
  if [ -z "$device" ]; then
    echo "No online ADB device found."
    echo "Current adb devices output:"
    adb devices -l || true
    echo "Connect/authorize a device and ensure it shows as 'device' before running this script."
    exit 1
  fi
  echo "$device"
}

RUN_CONNECTED_TESTS=false

if [ "$#" -gt 1 ]; then
  usage
  exit 1
fi

if [ "$#" -eq 1 ]; then
  case "$1" in
    --connected-tests)
      RUN_CONNECTED_TESTS=true
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      usage
      exit 1
      ;;
  esac
fi

ensure_cmd adb

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

# 2. Find the APK path
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
  echo "APK not found at $APK_PATH"
  exit 1
fi

echo "APK built at $APK_PATH"


# 3. Find first connected online device (USB or TCP)
DEVICE="$(require_online_device)"

PACKAGE_NAME="com.zebra.rfid.rwdemo2"

echo "Killing previous running app..."
adb -s "$DEVICE" shell am force-stop "$PACKAGE_NAME" || true

echo "Deleting (uninstalling) existing app..."
adb -s "$DEVICE" uninstall "$PACKAGE_NAME" || true

echo "Deploying APK to device $DEVICE..."
adb -s "$DEVICE" install -r "$APK_PATH"

echo "Launching app..."
adb -s "$DEVICE" shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1

echo "App launched on $DEVICE."

if [ "$RUN_CONNECTED_TESTS" = true ]; then
  echo "Running connected Android instrumentation tests on $DEVICE..."
  ./gradlew connectedDebugAndroidTest
  echo "Connected Android instrumentation tests completed."
fi
