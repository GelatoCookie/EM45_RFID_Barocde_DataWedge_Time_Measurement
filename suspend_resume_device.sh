#!/bin/bash
# Suspend or resume a connected Android device over ADB.

set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  ./suspend_resume_device.sh suspend [device_serial]
  ./suspend_resume_device.sh resume [device_serial]

Examples:
  ./suspend_resume_device.sh suspend
  ./suspend_resume_device.sh resume 192.168.1.50:5555
EOF
}

ensure_cmd() {
  local cmd="$1"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Required command not found: $cmd"
    exit 1
  fi
}

pick_device() {
  local explicit_device="${1:-}"
  if [ -n "$explicit_device" ]; then
    echo "$explicit_device"
    return
  fi

  adb start-server >/dev/null
  local detected
  detected=$(adb devices | awk 'NR>1 && $2=="device" {print $1; exit}')
  if [ -z "$detected" ]; then
    echo "No online ADB device found. Connect/authorize a device first." >&2
    exit 1
  fi
  echo "$detected"
}

main() {
  ensure_cmd adb

  if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
    usage
    exit 1
  fi

  local action="$1"
  local device
  device=$(pick_device "${2:-}")

  case "$action" in
    suspend)
      echo "Suspending device: $device"
      # KEYCODE_SLEEP: turns screen off without toggling unpredictably.
      adb -s "$device" shell input keyevent 223
      echo "Suspend command sent."
      ;;
    resume)
      echo "Resuming device: $device"
      # KEYCODE_WAKEUP wakes device; MENU helps reveal keyguard on many builds.
      adb -s "$device" shell input keyevent 224
      adb -s "$device" shell input keyevent 82 || true
      echo "Resume command sent."
      ;;
    *)
      echo "Invalid action: $action"
      usage
      exit 1
      ;;
  esac
}

main "$@"
