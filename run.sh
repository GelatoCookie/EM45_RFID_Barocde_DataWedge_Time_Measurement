#!/bin/bash
# Deploy and run app on connected device

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

find_online_device() {
  adb start-server >/dev/null 2>&1 || true
  adb devices | awk 'NR>1 && $2=="device" {print $1; exit}'
}

require_online_device() {
  local device
  device="$(find_online_device)"
  if [ -z "$device" ]; then
    echo "Error: No online ADB device found."
    echo "Current adb devices output:"
    adb devices -l || true
    echo ""
    echo "Connect/authorize a device and ensure it shows as 'device' before running this script."
    exit 1
  fi
  echo "$device"
}

usage() {
  cat <<'EOF'
Usage:
  ./run.sh [options]

Options:
  -h, --help          Show this help message
  -c, --clean         Run clean before build
  -s, --skip-build    Skip build step (use pre-built APK)
EOF
}

ensure_cmd adb

SKIP_BUILD=false
RUN_CLEAN=false

while [ $# -gt 0 ]; do
  case "$1" in
    -h|--help)
      usage
      exit 0
      ;;
    -c|--clean)
      RUN_CLEAN=true
      shift
      ;;
    -s|--skip-build)
      SKIP_BUILD=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      usage
      exit 1
      ;;
  esac
done

# Optional: clean first
if [ "$RUN_CLEAN" = true ]; then
  echo "Running clean..."
  ./clean.sh
fi

# Build APK unless skipped
if [ "$SKIP_BUILD" = false ]; then
  echo "Building APK..."
  ./build.sh
fi

# Find/verify APK
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
  echo "Error: APK not found at $APK_PATH"
  exit 1
fi

echo "APK ready at $APK_PATH"

# Find connected device
DEVICE="$(require_online_device)"
echo "Target device: $DEVICE"

PACKAGE_NAME="com.zebra.rfid.rwdemo2"

# Stop and uninstall existing app
echo "Stopping and uninstalling existing app..."
adb -s "$DEVICE" shell am force-stop "$PACKAGE_NAME" || true
adb -s "$DEVICE" uninstall "$PACKAGE_NAME" || true

# Install APK
echo "Installing APK to device $DEVICE..."
adb -s "$DEVICE" install -r "$APK_PATH"

# Launch app
echo "Launching app..."
adb -s "$DEVICE" shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1

echo "✓ App launched on $DEVICE"
