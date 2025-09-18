#!/bin/bash

# Firebase App Distribution Test Script
echo "🧪 Firebase App Distribution Test"
echo "================================="

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI not found. Installing..."
    npm install -g firebase-tools
fi

echo "✅ Firebase CLI version: $(firebase --version)"

# Check if APK exists
if [ ! -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo "❌ APK not found. Building first..."
    ./gradlew assembleRelease
    if [ $? -ne 0 ]; then
        echo "❌ Build failed"
        exit 1
    fi
fi

echo "✅ APK found: app/build/outputs/apk/release/app-release.apk"

# Check environment variables
if [ -z "$FIREBASE_APP_ID" ]; then
    echo "❌ FIREBASE_APP_ID environment variable not set"
    echo "   Set it with: export FIREBASE_APP_ID=your_app_id"
    exit 1
fi

if [ -z "$FIREBASE_TOKEN" ]; then
    echo "❌ FIREBASE_TOKEN environment variable not set"
    echo "   Set it with: export FIREBASE_TOKEN=your_token"
    echo "   Get token with: firebase login:ci"
    exit 1
fi

if [ -z "$FIREBASE_PROJECT_ID" ]; then
    echo "❌ FIREBASE_PROJECT_ID environment variable not set"
    echo "   Set it with: export FIREBASE_PROJECT_ID=your_project_id"
    exit 1
fi

echo "✅ Environment variables set"

# Set Firebase project
echo "🔧 Setting Firebase project..."
firebase use "$FIREBASE_PROJECT_ID" --token "$FIREBASE_TOKEN"

if [ $? -ne 0 ]; then
    echo "❌ Failed to set Firebase project"
    echo "   Check your FIREBASE_PROJECT_ID and FIREBASE_TOKEN"
    exit 1
fi

echo "✅ Firebase project set to $FIREBASE_PROJECT_ID"

# Test distribution
echo "📱 Testing Firebase App Distribution..."
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app "$FIREBASE_APP_ID" \
  --groups "testers" \
  --release-notes "Test distribution from script" \
  --token "$FIREBASE_TOKEN"

if [ $? -eq 0 ]; then
    echo "✅ APK distributed successfully!"
    echo "📧 Check your email for the distribution link"
    echo "🔗 Or check Firebase Console: https://console.firebase.google.com/"
else
    echo "❌ Distribution failed"
    echo "   Check the error messages above"
    echo "   Common issues:"
    echo "   - Invalid App ID"
    echo "   - Invalid token"
    echo "   - App not found in Firebase project"
    echo "   - Insufficient permissions"
fi