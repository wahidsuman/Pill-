#!/bin/bash

# Firebase Deployment Script for PillTracker Android App
echo "🔥 Firebase Deployment Script"
echo "============================="

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI not found. Installing..."
    npm install -g firebase-tools
fi

echo "✅ Firebase CLI version: $(firebase --version)"

# Check if service account file exists
if [ -f "firebase-service-account.json" ]; then
    echo "✅ Service account file found"
    export GOOGLE_APPLICATION_CREDENTIALS=firebase-service-account.json
else
    echo "⚠️  Service account file not found. Using token authentication."
fi

# Check if Firebase project is initialized
if [ ! -f "firebase.json" ]; then
    echo "❌ Firebase project not initialized. Creating configuration..."
    
    # Create firebase.json
    cat > firebase.json << EOF
{
  "projects": {
    "default": "pilltracker-android"
  },
  "hosting": {
    "public": "public",
    "ignore": [
      "firebase.json",
      "**/.*",
      "**/node_modules/**"
    ],
    "rewrites": [
      {
        "source": "**",
        "destination": "/index.html"
      }
    ]
  },
  "firestore": {
    "rules": "firestore.rules",
    "indexes": "firestore.indexes.json"
  },
  "storage": {
    "rules": "storage.rules"
  }
}
EOF

    # Create .firebaserc
    cat > .firebaserc << EOF
{
  "projects": {
    "default": "pilltracker-android"
  }
}
EOF

    echo "✅ Firebase configuration created"
fi

# Set Firebase project
echo "🔧 Setting Firebase project..."
if [ -n "$FIREBASE_TOKEN" ]; then
    firebase use pilltracker-android --token "$FIREBASE_TOKEN"
else
    echo "⚠️  FIREBASE_TOKEN not set. You may need to authenticate manually."
    firebase use pilltracker-android
fi

# Deploy Firebase services
echo "🚀 Deploying Firebase services..."

# Deploy hosting
echo "📦 Deploying hosting..."
firebase deploy --only hosting

# Deploy Firestore rules
echo "📋 Deploying Firestore rules..."
firebase deploy --only firestore:rules

# Deploy Firestore indexes
echo "📊 Deploying Firestore indexes..."
firebase deploy --only firestore:indexes

# Deploy Storage rules
echo "🗄️  Deploying Storage rules..."
firebase deploy --only storage

echo "✅ Firebase deployment completed!"
echo ""
echo "🌐 Your app is now available at:"
echo "   https://pilltracker-android.web.app"
echo ""
echo "📱 Android app features:"
echo "   - Complete UI matching the design"
echo "   - Firebase backend integration"
echo "   - Real-time data synchronization"
echo "   - Cloud storage for medication data"