# Pill Reminder - APK Build and Distribution

This document explains how to build APKs and distribute them to Firebase App Distribution.

## 🚀 Automated Deployment (GitHub Actions)

### Prerequisites

Make sure you have the following GitHub Secrets configured in your repository:

1. **FIREBASE_TOKEN** - Firebase CLI token
2. **FIREBASE_APP_ID** - Firebase App ID for distribution
3. **FIREBASE_PROJECT_ID** - Firebase project ID
4. **FIREBASE_SERVICE_ACCOUNT_KEY** - Firebase service account JSON
5. **GOOGLE_SERVICES_JSON** - Google services configuration JSON
6. **TESTER_EMAILS** - Comma-separated list of tester emails

### Workflow Triggers

The workflow runs automatically on:
- Push to `main` branch
- Push to `develop` branch
- Pull requests to `main` branch
- Manual trigger via GitHub Actions UI

### Workflow Steps

1. **Setup Environment**
   - Checkout repository
   - Setup JDK 17
   - Setup Android SDK
   - Cache Gradle dependencies

2. **Build APKs**
   - Build debug APK (`app-debug.apk`)
   - Build release APK (`app-release.apk`)
   - Upload both as artifacts

3. **Firebase Distribution**
   - Setup Firebase CLI
   - Create service account file
   - Set Firebase project
   - Distribute release APK to testers

4. **Notifications**
   - Comment on PRs with build status
   - Notify testers via Firebase

## 🛠️ Manual Deployment

### Prerequisites

1. **Environment Variables**
   ```bash
   export FIREBASE_TOKEN="your_firebase_token"
   export FIREBASE_APP_ID="your_app_id"
   export FIREBASE_PROJECT_ID="your_project_id"
   export FIREBASE_SERVICE_ACCOUNT_KEY="your_service_account_json"
   export TESTER_EMAILS="tester1@example.com,tester2@example.com"
   ```

2. **Google Services File**
   - Add your `google-services.json` to `app/` directory
   - Or use the template: `app/google-services.json.template`

### Manual Build and Deploy

```bash
# Make the script executable
chmod +x deploy-apk.sh

# Run the deployment script
./deploy-apk.sh
```

### Manual Steps

```bash
# 1. Build APKs
./gradlew assembleDebug
./gradlew assembleRelease

# 2. Setup Firebase
npm install -g firebase-tools
firebase use $FIREBASE_PROJECT_ID --token $FIREBASE_TOKEN

# 3. Distribute APK
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app $FIREBASE_APP_ID \
  --groups "testers" \
  --release-notes "Manual build" \
  --token $FIREBASE_TOKEN
```

## 📱 Firebase App Distribution Setup

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing
3. Enable App Distribution

### 2. Add Android App
1. Click "Add app" → Android
2. Package name: `com.pillreminder.app`
3. Download `google-services.json`
4. Note the App ID for distribution

### 3. Create Tester Groups
1. Go to App Distribution → Testers & Groups
2. Create a group called "testers"
3. Add tester emails

### 4. Get Firebase Token
```bash
firebase login:ci
# Copy the token and add to GitHub Secrets
```

## 🔧 Configuration Files

### Firebase Configuration (`firebase.json`)
```json
{
  "projects": {
    "default": "your-project-id"
  },
  "appdistribution": {
    "app": "your-app-id",
    "groups": ["testers"],
    "releaseNotes": "Automated build from GitHub Actions"
  }
}
```

### Google Services Template
Use `app/google-services.json.template` as a reference for your actual `google-services.json` file.

## 🐛 Troubleshooting

### Common Issues

1. **Build Fails**
   - Check if all dependencies are properly configured
   - Verify `google-services.json` is in the correct location
   - Ensure Gradle wrapper is executable

2. **Firebase Distribution Fails**
   - Verify Firebase token is valid
   - Check if App ID is correct
   - Ensure tester group exists

3. **Permission Issues**
   - Make sure `gradlew` is executable: `chmod +x gradlew`
   - Verify Firebase service account has proper permissions

### Debug Commands

```bash
# Check Firebase CLI version
firebase --version

# List Firebase projects
firebase projects:list

# Check app distribution status
firebase appdistribution:list --app $FIREBASE_APP_ID
```

## 📋 Checklist

Before deploying:

- [ ] All GitHub Secrets are configured
- [ ] `google-services.json` is in `app/` directory
- [ ] Firebase project has App Distribution enabled
- [ ] Tester group is created with valid emails
- [ ] Firebase token has proper permissions
- [ ] App ID is correct for distribution

## 🎯 Next Steps

1. **Test the workflow** by pushing to a branch
2. **Verify APK distribution** in Firebase Console
3. **Test the app** on physical devices
4. **Set up additional tester groups** as needed
5. **Configure release notes** automation