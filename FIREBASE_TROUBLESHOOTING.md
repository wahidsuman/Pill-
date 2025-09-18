# Firebase App Distribution Troubleshooting Guide

## 🚨 Common Issues and Solutions

### 1. **App Not Appearing in Firebase App Distribution**

#### **Check GitHub Secrets**
Make sure these secrets are properly configured in your GitHub repository:

1. Go to: **Settings → Secrets and variables → Actions**
2. Verify these secrets exist:
   - `FIREBASE_APP_ID` - Your Firebase App ID
   - `FIREBASE_TOKEN` - Your Firebase CLI token
   - `FIREBASE_PROJECT_ID` - Your Firebase project ID
   - `TESTER_EMAILS` - Comma-separated list of tester emails

#### **Get Correct Firebase App ID**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **App Distribution** in the left sidebar
4. Click on your Android app
5. Copy the **App ID** (format: `1:123456789:android:abcdef123456`)

#### **Get Firebase Token**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Get your token
firebase login:ci
# Copy the token and add it to FIREBASE_TOKEN secret
```

### 2. **"Invalid project selection" Error**

This means your `FIREBASE_PROJECT_ID` is incorrect.

#### **Get Correct Project ID**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Project Settings** (gear icon)
4. Copy the **Project ID** (not the project name)

#### **Verify Project Access**
Make sure your Firebase account has access to the project:
1. Check if you're added as a member
2. Verify you have the correct permissions
3. Try accessing the project in Firebase Console

### 3. **"App not found" Error**

This means your `FIREBASE_APP_ID` is incorrect or the app doesn't exist.

#### **Create Android App in Firebase**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click **Add app** → **Android**
4. Package name: `com.pillreminder.app`
5. App nickname: `Pill Reminder`
6. Download `google-services.json`
7. Note the **App ID** from the console

#### **Enable App Distribution**
1. In Firebase Console, go to **App Distribution**
2. Click **Get started**
3. Select your Android app
4. Follow the setup instructions

### 4. **"Insufficient permissions" Error**

Your Firebase token doesn't have the required permissions.

#### **Fix Permissions**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Project Settings** → **Service accounts**
4. Click **Generate new private key**
5. Download the JSON file
6. Add it to `FIREBASE_SERVICE_ACCOUNT_KEY` secret

### 5. **Testers Not Receiving Notifications**

#### **Check Tester Groups**
1. Go to Firebase Console → **App Distribution**
2. Click **Testers & Groups**
3. Create a group called "testers"
4. Add tester email addresses
5. Make sure testers accept the invitation

#### **Check Email Settings**
- Testers should check spam folder
- Make sure email addresses are correct
- Testers need to accept the invitation first

### 6. **Manual Distribution (Fallback)**

If Firebase distribution fails, you can distribute manually:

#### **Download APK from GitHub**
1. Go to your GitHub repository
2. Click **Actions** tab
3. Click on the latest workflow run
4. Scroll down to **Artifacts**
5. Download `app-release-apk` or `app-debug-apk`

#### **Share APK with Testers**
- Upload to Google Drive, Dropbox, or similar
- Share the download link with testers
- Testers can install the APK directly

## 🔧 Debug Steps

### 1. **Check Workflow Logs**
1. Go to GitHub → **Actions**
2. Click on the latest workflow run
3. Look for the "Distribute APK to Firebase App Distribution" step
4. Check for error messages

### 2. **Test Firebase CLI Locally**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# Set project
firebase use YOUR_PROJECT_ID

# Test distribution
firebase appdistribution:distribute app-release.apk \
  --app YOUR_APP_ID \
  --groups "testers" \
  --release-notes "Test distribution"
```

### 3. **Verify Secrets**
```bash
# Check if secrets are set (in GitHub Actions)
echo "App ID: ${{ secrets.FIREBASE_APP_ID }}"
echo "Token: ${{ secrets.FIREBASE_TOKEN }}"
echo "Project: ${{ secrets.FIREBASE_PROJECT_ID }}"
```

## 📱 Quick Setup Checklist

- [ ] Firebase project created
- [ ] Android app added to Firebase project
- [ ] App Distribution enabled
- [ ] Tester group created with email addresses
- [ ] GitHub secrets configured:
  - [ ] `FIREBASE_APP_ID`
  - [ ] `FIREBASE_TOKEN`
  - [ ] `FIREBASE_PROJECT_ID`
  - [ ] `TESTER_EMAILS`
- [ ] Testers invited and accepted invitations
- [ ] Workflow runs successfully
- [ ] Check Firebase Console for distributed apps

## 🆘 Still Having Issues?

1. **Check the latest workflow run** for detailed error messages
2. **Verify all secrets** are correctly set
3. **Test Firebase CLI locally** to isolate the issue
4. **Check Firebase Console** to see if the app appears there
5. **Contact Firebase support** if the issue persists

## 📞 Support Resources

- [Firebase App Distribution Documentation](https://firebase.google.com/docs/app-distribution)
- [Firebase Console](https://console.firebase.google.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)