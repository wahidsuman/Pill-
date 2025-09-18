# Firebase Setup for PillTracker Android App

This document explains how to set up Firebase for the PillTracker Android app, including CI/CD configuration.

## 🔥 Firebase Services Used

- **Firestore**: Database for medication and reminder data
- **Storage**: File storage for medication images
- **Hosting**: Web hosting for the app landing page
- **Authentication**: User authentication (optional)

## 📋 Prerequisites

1. **Firebase CLI**: Install globally
   ```bash
   npm install -g firebase-tools
   ```

2. **Firebase Project**: Create a project at [Firebase Console](https://console.firebase.google.com/)

3. **Service Account**: Generate a service account key for CI/CD

## 🚀 Local Setup

### 1. Initialize Firebase Project

```bash
# Login to Firebase
firebase login

# Initialize Firebase in your project
firebase init

# Select the following services:
# - Firestore
# - Storage  
# - Hosting
```

### 2. Configure Firebase Project

```bash
# Set your Firebase project
firebase use your-project-id

# Deploy configuration
firebase deploy
```

## 🔧 CI/CD Setup

### Environment Variables

Set these environment variables in your CI/CD system:

```bash
# Firebase token for authentication
FIREBASE_TOKEN=your-firebase-token

# Service account JSON (for service account authentication)
FIREBASE_SERVICE_ACCOUNT='{"type":"service_account",...}'
```

### GitHub Actions

The project includes a GitHub Actions workflow (`.github/workflows/firebase-deploy.yml`) that:

1. Installs Firebase CLI
2. Sets up authentication
3. Deploys Firebase services
4. Builds Android APK
5. Uploads artifacts

### Manual CI/CD Script

Use the provided script for manual CI/CD setup:

```bash
# Run the CI setup script
./ci-firebase-setup.sh

# Deploy to Firebase
firebase deploy --token $FIREBASE_TOKEN
```

## 📱 Android Integration

### 1. Add Firebase to Android Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click "Add app" → Android
4. Enter package name: `com.pilltracker`
5. Download `google-services.json`
6. Place it in `app/` directory

### 2. Add Firebase Dependencies

Add to `app/build.gradle`:

```gradle
dependencies {
    // Firebase BOM
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    
    // Firebase services
    implementation 'com.google.firebase:firebase-firestore'
    implementation 'com.google.firebase:firebase-storage'
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-analytics'
}
```

### 3. Apply Google Services Plugin

Add to `app/build.gradle`:

```gradle
apply plugin: 'com.google.gms.google-services'
```

Add to project-level `build.gradle`:

```gradle
dependencies {
    classpath 'com.google.gms:google-services:4.4.0'
}
```

## 🗄️ Database Structure

### Firestore Collections

```
medications/
├── {medicationId}/
│   ├── name: string
│   ├── dosage: string
│   ├── frequency: string
│   ├── times: array
│   ├── userId: string
│   └── createdAt: timestamp

reminders/
├── {reminderId}/
│   ├── medicationId: string
│   ├── scheduledTime: timestamp
│   ├── takenTime: timestamp
│   ├── isTaken: boolean
│   ├── userId: string
│   └── notes: string
```

### Storage Structure

```
medication-images/
├── {userId}/
│   └── {medicationId}/
│       └── image.jpg
```

## 🔒 Security Rules

### Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read/write access to authenticated users
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
    
    // Allow public read access to medications (for demo)
    match /medications/{medicationId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

### Storage Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow read/write access to authenticated users
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🚀 Deployment Commands

### Deploy All Services

```bash
firebase deploy
```

### Deploy Specific Services

```bash
# Deploy only hosting
firebase deploy --only hosting

# Deploy only Firestore rules
firebase deploy --only firestore:rules

# Deploy only Storage rules
firebase deploy --only storage
```

### Deploy with Token (CI/CD)

```bash
firebase deploy --token $FIREBASE_TOKEN
```

## 🔍 Troubleshooting

### Common Issues

1. **"firebase use must be run from a Firebase project directory"**
   - Solution: Run `firebase init` first or ensure `firebase.json` exists

2. **Authentication errors**
   - Solution: Use `firebase login` or set `FIREBASE_TOKEN`

3. **Service account errors**
   - Solution: Set `GOOGLE_APPLICATION_CREDENTIALS` environment variable

### Debug Commands

```bash
# Check Firebase project status
firebase projects:list

# Check current project
firebase use

# Check Firebase CLI version
firebase --version

# Check authentication status
firebase login:list
```

## 📚 Additional Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase CLI Reference](https://firebase.google.com/docs/cli)
- [Firebase Security Rules](https://firebase.google.com/docs/rules)
- [Android Firebase Setup](https://firebase.google.com/docs/android/setup)

## 🎯 Next Steps

1. Set up Firebase project in console
2. Configure environment variables in CI/CD
3. Add Firebase to Android project
4. Deploy Firebase services
5. Test the integration

The app is now ready for Firebase integration! 🎉