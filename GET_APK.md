# 📱 How to Download APK from GitHub Actions

Your repository is already set up to build APKs automatically! Every time you push to the `main` branch, GitHub Actions builds both **debug** and **release** APKs.

## 🚀 Steps to Download APK:

### 1. Go to GitHub Actions
- Visit: https://github.com/wahidsuman/Pill- (your repository)
- Click on the **"Actions"** tab at the top

### 2. Find the Latest Build
- Look for **"Build APK and Distribute to Firebase"** workflow
- Click on the most recent successful run (green checkmark ✅)

### 3. Download the APK
- Scroll down to the **"Artifacts"** section at the bottom
- You'll see:
  - **📦 app-debug-apk** - Debug version (for testing)
  - **📦 app-release-apk** - Release version (for production)
- Click on the artifact you want to download

### 4. Install on Your Phone
- Extract the downloaded ZIP file
- Transfer the APK to your Android device
- Enable "Install from Unknown Sources" in Settings
- Open and install the APK

## 📋 What Triggers APK Build?

The APK builds automatically when you:
- ✅ Push to `main` branch
- ✅ Push to `develop` branch
- ✅ Create a Pull Request to `main`
- ✅ Manually trigger workflow (Actions tab → Build APK → Run workflow)

## 📦 What's Built?

Each build creates TWO APKs:

### Debug APK (`app-debug-apk`)
- Easier to test
- Includes debug information
- Larger file size
- **Use this for testing**

### Release APK (`app-release-apk`)
- Optimized for production
- Smaller file size
- Better performance
- **Use this for distribution**

## 🔄 Current Status

✅ **Workflow is ACTIVE and configured!**

Latest commit will trigger a new build with:
- Modern wheel time picker
- Dynamic "Add Time" button
- All your latest features
- Real-time statistics
- Image upload support
- Custom day selection

## 💡 Pro Tip

To manually trigger a build without pushing code:
1. Go to **Actions** tab
2. Click **"Build APK and Distribute to Firebase"**
3. Click **"Run workflow"** button
4. Select `main` branch
5. Click **"Run workflow"** green button

Your APK will be built in ~5-10 minutes!

---

**Note:** The APK artifacts are stored for 90 days by default on GitHub.
