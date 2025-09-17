# PillTracker - Android App

A modern Android pill reminder app built with Jetpack Compose, following the same design and structure as the React version.

## Features

- **Modern UI**: Built with Jetpack Compose and Material 3 design
- **Pill Management**: Add, edit, and delete medications
- **Reminder System**: Set multiple reminder times for each medication
- **Visual Indicators**: Color-coded pills with taken/not taken status
- **Statistics**: Track taken, pending, and total pills
- **Local Storage**: Room database for data persistence

## Architecture

- **MVVM Pattern**: Clean separation of concerns
- **Jetpack Compose**: Modern declarative UI
- **Room Database**: Local data persistence
- **Hilt**: Dependency injection
- **Material 3**: Modern design system

## Project Structure

```
app/
├── src/main/java/com/pilltracker/app/
│   ├── data/
│   │   ├── model/Pill.kt
│   │   ├── dao/PillDao.kt
│   │   ├── database/PillDatabase.kt
│   │   ├── repository/PillRepository.kt
│   │   └── converter/Converters.kt
│   ├── ui/
│   │   ├── screen/PillTrackerScreen.kt
│   │   ├── components/
│   │   │   ├── PillCard.kt
│   │   │   ├── PillIcon.kt
│   │   │   └── AddPillModal.kt
│   │   ├── viewmodel/PillViewModel.kt
│   │   └── theme/
│   ├── di/DatabaseModule.kt
│   ├── MainActivity.kt
│   └── PillTrackerApplication.kt
└── src/main/res/
    ├── values/
    ├── drawable/
    └── mipmap-*/
```

## Building the App

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK 34
- Minimum SDK 24

### Local Build

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run

### Command Line Build

```bash
./gradlew assembleDebug
```

## GitHub Actions

The project includes a GitHub Actions workflow that automatically builds APK files when you push to the main branch or create a pull request.

### Browser-based Live Preview (BrowserStack App Live)

Preview builds in your browser (tablet/phone/desktop) using BrowserStack App Live.

Setup:
- In GitHub repo settings → Secrets and variables → Actions, add:
  - `BROWSERSTACK_USERNAME`
  - `BROWSERSTACK_ACCESS_KEY`
- Ensure the workflow `.github/workflows/browserstack-app-live.yml` exists.

Usage:
- Push to `main` or manually trigger the workflow (Actions → BrowserStack App Live Upload → Run workflow).
- After it finishes, open BrowserStack App Live and select the latest uploaded app under “Uploaded apps”.
- Launch on any BrowserStack device to see the app without downloading APKs.

### Download APK

1. Go to the Actions tab in your GitHub repository
2. Click on the latest workflow run
3. Download the APK from the Artifacts section:
   - `pilltracker-debug-apk` - Debug version
   - `pilltracker-release-apk` - Release version
   - `pilltracker-debug-bundle` - Debug AAB bundle
   - `pilltracker-release-bundle` - Release AAB bundle

## Features Matching React Version

✅ **Header Section**: Shows app name, current date, and time  
✅ **Quick Stats**: Displays taken, pending, and total pill counts  
✅ **Upcoming Reminders**: Shows next 2 upcoming medications  
✅ **Pill List**: Displays all medications with status indicators  
✅ **Add Pill Modal**: Form to add new medications  
✅ **Color-coded Pills**: Visual distinction with different colors  
✅ **Time Management**: Multiple reminder times per medication  
✅ **Mark as Taken**: Toggle medication status  

## Future Enhancements

- Push notifications for reminders
- Medication history tracking
- Export/import functionality
- Dark theme support
- Medication photos
- Dosage tracking
- Refill reminders

## Dependencies

- **Jetpack Compose**: Modern UI toolkit
- **Room**: Local database
- **Hilt**: Dependency injection
- **Material 3**: Design system
- **Navigation Compose**: Navigation
- **Work Manager**: Background tasks
- **Kotlinx DateTime**: Date/time handling

## License

This project is open source and available under the MIT License.# Trigger CI build
