#!/bin/bash

# PillTracker Android App Setup Script
echo "🏥 PillTracker Android App Setup"
echo "================================="

# Check if Android SDK is available
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME environment variable is not set"
    echo "Please set ANDROID_HOME to your Android SDK location"
    echo "Example: export ANDROID_HOME=/path/to/android-sdk"
    echo ""
    echo "You can download Android SDK from:"
    echo "https://developer.android.com/studio#command-tools"
    exit 1
fi

# Check if Android SDK exists
if [ ! -d "$ANDROID_HOME" ]; then
    echo "❌ Android SDK not found at: $ANDROID_HOME"
    echo "Please install Android SDK and set ANDROID_HOME correctly"
    exit 1
fi

echo "✅ Android SDK found at: $ANDROID_HOME"

# Update local.properties with correct SDK path
echo "sdk.dir=$ANDROID_HOME" > local.properties
echo "✅ Updated local.properties with SDK path"

# Make gradlew executable
chmod +x ./gradlew
echo "✅ Made gradlew executable"

# Test Gradle wrapper
echo "🔧 Testing Gradle wrapper..."
./gradlew --version

# Try to build the project
echo "🏗️  Building the project..."
if ./gradlew build; then
    echo "✅ Build successful!"
    echo ""
    echo "🎉 PillTracker app is ready!"
    echo "You can now:"
    echo "  - Open the project in Android Studio"
    echo "  - Run: ./gradlew assembleDebug"
    echo "  - Install on device: ./gradlew installDebug"
else
    echo "❌ Build failed. Please check the error messages above."
    echo "Common issues:"
    echo "  - Android SDK not properly installed"
    echo "  - Missing Android SDK components"
    echo "  - Java version compatibility issues"
fi