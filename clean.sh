#!/bin/bash
# Clean build artifacts and gradle caches

set -euo pipefail

WORKDIR="$(cd "$(dirname "$0")" && pwd)"
cd "$WORKDIR"

echo "Cleaning gradle build artifacts..."
./gradlew clean

echo "Removing gradle wrapper JAR if corrupted..."
rm -f gradle/wrapper/gradle-wrapper.jar

echo "Clean complete."
