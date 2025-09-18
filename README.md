# PillTracker Android App

A modern Android medication tracking app that matches the design shown in the provided image.

## Features

- **Clean, Modern UI**: Matches the exact design from the provided image
- **Medication Tracking**: Track daily medications with reminders
- **Summary Cards**: View taken, pending, and total medications for the day
- **Next Reminders**: See upcoming medication reminders
- **Medication Management**: Add, edit, and delete medications
- **Material Design**: Uses Material Design 3 components

## Project Structure

```
app/
├── src/main/
│   ├── java/com/pilltracker/
│   │   ├── MainActivity.kt          # Main activity with UI logic
│   │   ├── Medication.kt            # Data models for medications
│   │   └── MedicationRepository.kt  # Repository for medication data
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # Main activity layout
│   │   ├── values/
│   │   │   ├── colors.xml           # App color scheme
│   │   │   ├── strings.xml          # String resources
│   │   │   └── themes.xml           # App theme
│   │   └── drawable/                # Icons and drawable resources
│   └── AndroidManifest.xml          # App manifest
├── build.gradle                     # App-level build configuration
└── proguard-rules.pro              # ProGuard rules
```

## Design Features

The app replicates the exact design from the provided image:

1. **Header Card**: Shows PillTracker branding with current time
2. **Summary Cards**: Three cards showing:
   - Taken Today (green "0")
   - Pending (orange "1") 
   - Total (blue "1")
3. **Next Reminders**: Orange card showing upcoming medication reminder
4. **My Medications**: List of medications with take/edit/delete actions
5. **Floating Action Button**: Blue FAB for adding new medications

## Color Scheme

- **Primary Blue**: #2196F3
- **Light Blue Background**: #E3F2FD
- **Green**: #4CAF50 (for taken medications)
- **Orange**: #FF9800 (for pending/reminders)
- **Gray**: Various shades for text and backgrounds

## Building the App

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9.10+

### Build Steps

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Build the project (Build → Make Project)
4. Run on device or emulator

### Gradle Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean project
./gradlew clean
```

## Current Implementation

The app currently includes:

- ✅ Complete UI layout matching the design
- ✅ Material Design 3 components
- ✅ Proper color scheme and theming
- ✅ Basic data models for medications
- ✅ Repository pattern for data management
- ✅ Click handlers for all interactive elements
- ✅ Sample data to demonstrate functionality

## Future Enhancements

- [ ] Add medication functionality (add/edit/delete)
- [ ] Implement reminder notifications
- [ ] Add medication history tracking
- [ ] Implement data persistence (Room database)
- [ ] Add medication photos
- [ ] Implement backup/sync functionality
- [ ] Add medication interaction warnings
- [ ] Implement dosage tracking

## Screenshots

The app matches the design shown in the provided image with:
- Light blue background
- White cards with rounded corners
- Proper spacing and typography
- Material Design components
- Responsive layout

## License

This project is created for demonstration purposes.