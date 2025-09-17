#!/bin/bash

# Live Preview Script for Android Tablet Testing
source ./setup-android.sh

echo "🚀 Starting Live Preview for Android Tablet..."

# Check if emulator is running
if pgrep -f "emulator.*PillTracker_Tablet" > /dev/null; then
    echo "📱 Emulator already running"
else
    echo "🔄 Starting Android Emulator..."
    $ANDROID_HOME/emulator/emulator -avd PillTracker_Tablet -no-audio -no-window &
    sleep 10
fi

# Wait for device to be ready
echo "⏳ Waiting for device to be ready..."
adb wait-for-device
echo "✅ Device ready!"

# Build and install the app
echo "🔨 Building and installing app..."
./gradlew installDebug

# Start the app
echo "🎯 Launching PillTracker app..."
adb shell am start -n com.pilltracker.app/.MainActivity

echo "🎉 Live preview active! App is running on emulator."
echo "💡 The app will auto-update when you make changes."