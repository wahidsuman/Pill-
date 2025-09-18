# PillTracker Simple Android App

A simple Android medication tracking app that matches the exact design from the provided image. This is a simplified version that focuses on the UI design shown in the image.

## Features

- **Exact Design Match**: Replicates the UI from the provided image perfectly
- **Clean, Modern UI**: Light blue background with white cards and proper spacing
- **Material Design**: Uses Material Design 3 components
- **Interactive Elements**: All buttons and FAB have click handlers
- **Responsive Layout**: Properly sized for mobile devices

## Design Features

The app replicates the exact design from the provided image:

1. **Header Card**: Shows PillTracker branding with current time (01:24 am)
2. **Summary Cards**: Three cards showing:
   - Taken Today (green "0")
   - Pending (orange "1") 
   - Total (blue "1")
3. **Next Reminders**: Orange card showing upcoming medication reminder (hdms at 14:02)
4. **My Medications**: List of medications with take/edit/delete actions
5. **Floating Action Button**: Blue FAB for adding new medications

## Color Scheme

- **Primary Blue**: #2196F3
- **Light Blue Background**: #E3F2FD
- **Green**: #4CAF50 (for taken medications)
- **Orange**: #FF9800 (for pending/reminders)
- **Gray**: Various shades for text and backgrounds

## Project Structure

```
app/
├── src/main/
│   ├── java/com/pilltracker/
│   │   └── SimpleMainActivity.kt      # Simple main activity with UI logic
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml     # Main activity layout matching the design
│   │   ├── values/
│   │   │   ├── colors.xml            # App color scheme
│   │   │   └── strings.xml           # String resources
│   │   └── drawable/                 # Icons and drawable resources
│   └── AndroidManifest.xml           # App manifest
├── build.gradle.kts                  # App-level build configuration
└── gradle files                      # Gradle wrapper and configuration
```

## Building the App

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9.10+
- Java 17+ (JDK)

### Quick Setup

1. **Set Android SDK path:**
   ```bash
   export ANDROID_HOME=/path/to/your/android-sdk
   ```

2. **Run the setup script:**
   ```bash
   ./setup_simple.sh
   ```

3. **Or manually:**
   ```bash
   # Make gradlew executable
   chmod +x ./gradlew
   
   # Update local.properties with your SDK path
   echo "sdk.dir=$ANDROID_HOME" > local.properties
   
   # Build the project
   ./gradlew build
   ```

### Running the Simple Version

To run the simple UI version that matches the design:

1. **In Android Studio**: Launch `SimpleMainActivity` instead of `MainActivity`
2. **Via ADB**: 
   ```bash
   adb shell am start -n com.pilltracker/.SimpleMainActivity
   ```

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

# List all available tasks
./gradlew tasks
```

## Current Implementation

The simple app includes:

- ✅ Complete UI layout matching the exact design
- ✅ Material Design 3 components
- ✅ Proper color scheme and theming
- ✅ All interactive elements with click handlers
- ✅ Sample data to demonstrate functionality
- ✅ Responsive layout for mobile devices

## Screenshots

The app matches the design shown in the provided image with:
- Light blue background (#E3F2FD)
- White cards with rounded corners
- Proper spacing and typography
- Material Design components
- Responsive layout

## Differences from Main App

This simple version focuses purely on the UI design and includes:
- **SimpleMainActivity**: Basic activity with click handlers
- **XML Layout**: Traditional Android layout (not Compose)
- **Static Data**: Sample data matching the design
- **Basic Functionality**: Toast messages for interactions

The main app includes more advanced features like:
- Compose UI
- Database integration
- Alarm services
- Complex navigation

## License

This project is created for demonstration purposes.