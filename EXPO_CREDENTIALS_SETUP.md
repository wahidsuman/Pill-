# Expo Auto-Publish Setup Guide

This guide explains how to set up automatic Expo publishing whenever code is pushed to the main branch.

## 🎯 How It Works

When you push code to the `main` branch that affects the `pill-reminder-expo` directory:
1. GitHub Actions automatically triggers
2. The Expo app is built and published
3. The update becomes immediately available in Expo Go
4. You can test changes in real-time on your device

## 🔐 Setting Up GitHub Secrets

To make this work, you need to add your Expo credentials as **GitHub Secrets**:

### Method 1: Using Expo Access Token (Recommended)

1. **Get an Expo Access Token:**
   - Go to https://expo.dev/accounts/[your-username]/settings/access-tokens
   - Click "Create Token"
   - Give it a name like "GitHub Actions"
   - Copy the token

2. **Add to GitHub Secrets:**
   - Go to your GitHub repository
   - Click **Settings** → **Secrets and variables** → **Actions**
   - Click **New repository secret**
   - Name: `EXPO_TOKEN`
   - Value: Paste your Expo access token
   - Click **Add secret**

### Method 2: Using Username and Password

**⚠️ Note: This method is less secure. Use Method 1 if possible.**

1. **Add Username:**
   - Go to your GitHub repository
   - Click **Settings** → **Secrets and variables** → **Actions**
   - Click **New repository secret**
   - Name: `EXPO_USERNAME`
   - Value: `wahidsuman`
   - Click **Add secret**

2. **Add Password:**
   - Click **New repository secret** again
   - Name: `EXPO_PASSWORD`
   - Value: Your Expo password
   - Click **Add secret**

## ✅ Current Setup

- **Expo Owner:** wahidsuman
- **App Slug:** pill-reminder-expo
- **GitHub Workflow:** `.github/workflows/expo-publish.yml`

## 🚀 Testing the Workflow

1. Make a code change in the `pill-reminder-expo` directory
2. Commit and push to main branch (or let the environment do it)
3. Go to GitHub → **Actions** tab
4. Watch the "Expo Publish to Expo Go" workflow run
5. Once completed, open Expo Go on your device
6. Search for: `@wahidsuman/pill-reminder-expo`
7. The latest changes will be available!

## 📱 Accessing Your App in Expo Go

**Option 1: Search in Expo Go**
- Open Expo Go app
- Search for: `@wahidsuman/pill-reminder-expo`

**Option 2: Direct Link**
- URL: `exp://exp.host/@wahidsuman/pill-reminder-expo`
- Or visit: https://expo.dev/@wahidsuman/pill-reminder-expo

**Option 3: QR Code**
- Go to https://expo.dev/@wahidsuman/pill-reminder-expo
- Scan the QR code with Expo Go

## 🔄 Workflow

```
You Request Changes → I Make Changes → Push to Main → GitHub Actions → Expo Publish → Expo Go Updated
```

## 📝 Notes

- The workflow only triggers when files in `pill-reminder-expo/` directory change
- You can also manually trigger the workflow from the GitHub Actions tab
- Publishing typically takes 2-5 minutes
- Updates appear in Expo Go automatically (app may need to reload)

## 🛠 Troubleshooting

**If publishing fails:**
1. Check that GitHub Secrets are properly set
2. Verify your Expo account has access to publish
3. Check the GitHub Actions logs for specific errors

**If app doesn't update:**
1. Close and reopen Expo Go
2. Pull down to refresh in Expo Go
3. Check if the workflow completed successfully on GitHub

## 🔒 Security

- Never commit credentials directly to the repository
- Always use GitHub Secrets for sensitive information
- The workflow file is safe to commit (it only references secrets)
- Credentials are encrypted and only accessible during workflow execution
