#!/bin/bash

# Auto-sync with Live Preview
source ./setup-android.sh

echo "🔄 Auto-sync with live preview starting..."

# Sync from GitHub
echo "📥 Syncing from GitHub..."
git fetch origin
git pull origin main

# Check if emulator is running
if ! pgrep -f "emulator.*PillTracker_Tablet" > /dev/null; then
    echo "🚀 Starting emulator..."
    $ANDROID_HOME/emulator/emulator -avd PillTracker_Tablet -no-audio -no-window &
    sleep 15
fi

# Wait for device
adb wait-for-device

# Build and install
echo "🔨 Building and installing updated app..."
./gradlew installDebug

# Launch app
echo "🎯 Launching updated app..."
adb shell am start -n com.pilltracker.app/.MainActivity

echo "✅ Live preview updated! Changes are now visible on emulator."