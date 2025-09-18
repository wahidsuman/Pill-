#!/bin/bash

# APK Build and Firebase Distribution Script
echo "🚀 Pill Reminder APK Build and Distribution"
echo "=========================================="

# Check if required environment variables are set
if [ -z "$FIREBASE_TOKEN" ]; then
    echo "❌ FIREBASE_TOKEN environment variable not set"
    echo "Please set FIREBASE_TOKEN in your environment"
    exit 1
fi

if [ -z "$FIREBASE_APP_ID" ]; then
    echo "❌ FIREBASE_APP_ID environment variable not set"
    echo "Please set FIREBASE_APP_ID in your environment"
    exit 1
fi

if [ -z "$FIREBASE_PROJECT_ID" ]; then
    echo "❌ FIREBASE_PROJECT_ID environment variable not set"
    echo "Please set FIREBASE_PROJECT_ID in your environment"
    exit 1
fi

# Check if google-services.json exists, create from template if not
if [ ! -f "app/google-services.json" ]; then
    echo "⚠️  google-services.json not found, using template file"
    if [ -f "app/google-services.json.template" ]; then
        cp app/google-services.json.template app/google-services.json
        echo "✅ Template google-services.json copied"
    else
        echo "❌ google-services.json.template not found"
        echo "Please add your google-services.json file to app/ directory"
        exit 1
    fi
fi

echo "✅ Environment variables validated"

# Make gradlew executable
chmod +x ./gradlew

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug
if [ $? -eq 0 ]; then
    echo "✅ Debug APK built successfully"
else
    echo "❌ Debug APK build failed"
    exit 1
fi

# Build release APK
echo "🔨 Building release APK..."
./gradlew assembleRelease
if [ $? -eq 0 ]; then
    echo "✅ Release APK built successfully"
else
    echo "❌ Release APK build failed"
    exit 1
fi

# Setup Firebase CLI
echo "🔥 Setting up Firebase CLI..."
if ! command -v firebase &> /dev/null; then
    echo "Installing Firebase CLI..."
    npm install -g firebase-tools
fi

# Create Firebase service account file if provided
if [ -n "$FIREBASE_SERVICE_ACCOUNT_KEY" ]; then
    echo "📄 Creating Firebase service account file..."
    echo "$FIREBASE_SERVICE_ACCOUNT_KEY" > firebase-service-account.json
    export GOOGLE_APPLICATION_CREDENTIALS=firebase-service-account.json
    echo "✅ Service account file created"
fi

# Set Firebase project
echo "🔧 Setting Firebase project..."
firebase use "$FIREBASE_PROJECT_ID" --token "$FIREBASE_TOKEN"
echo "✅ Firebase project set to $FIREBASE_PROJECT_ID"

# Distribute APK to Firebase App Distribution
echo "📱 Distributing APK to Firebase App Distribution..."
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app "$FIREBASE_APP_ID" \
  --groups "testers" \
  --release-notes "Manual build from $(date)" \
  --token "$FIREBASE_TOKEN"

if [ $? -eq 0 ]; then
    echo "✅ APK distributed successfully!"
    echo ""
    echo "📱 App ID: $FIREBASE_APP_ID"
    echo "👥 Testers: $TESTER_EMAILS"
    echo "📝 Release notes: Manual build from $(date)"
    echo ""
    echo "🎉 Distribution completed!"
else
    echo "❌ APK distribution failed"
    exit 1
fi