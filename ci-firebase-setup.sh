#!/bin/bash

# CI/CD Firebase Setup Script
echo "🔥 CI/CD Firebase Setup"
echo "======================="

# Create Firebase service account file from environment variable
if [ -n "$FIREBASE_SERVICE_ACCOUNT" ]; then
    echo "📄 Creating Firebase service account file..."
    echo "$FIREBASE_SERVICE_ACCOUNT" > firebase-service-account.json
    export GOOGLE_APPLICATION_CREDENTIALS=firebase-service-account.json
    echo "✅ Service account file created"
else
    echo "⚠️  FIREBASE_SERVICE_ACCOUNT environment variable not set"
fi

# Set Firebase project using token
if [ -n "$FIREBASE_TOKEN" ]; then
    echo "🔧 Setting Firebase project with token..."
    firebase use firebase-project --token "$FIREBASE_TOKEN"
    echo "✅ Firebase project set successfully"
else
    echo "❌ FIREBASE_TOKEN environment variable not set"
    echo "Please set FIREBASE_TOKEN in your CI/CD environment"
    exit 1
fi

# Verify Firebase configuration
echo "🔍 Verifying Firebase configuration..."
if [ -f "firebase.json" ] && [ -f ".firebaserc" ]; then
    echo "✅ Firebase configuration files found"
    echo "📋 Project configuration:"
    cat .firebaserc
else
    echo "❌ Firebase configuration files missing"
    echo "Creating default configuration..."
    
    # Create .firebaserc
    cat > .firebaserc << EOF
{
  "projects": {
    "default": "firebase-project"
  }
}
EOF
    
    echo "✅ Default Firebase configuration created"
fi

echo "🎉 Firebase setup completed successfully!"
echo "Ready for deployment with: firebase deploy --token \$FIREBASE_TOKEN"