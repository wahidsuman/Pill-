# Pill Reminder Android App

A modern Android application built with Kotlin and Jetpack Compose for medication tracking and reminders.

## Project Structure

```
app/
├── src/main/
│   ├── java/com/pillreminder/app/
│   │   ├── PillReminderApplication.kt          # Application class
│   │   ├── ui/
│   │   │   ├── MainActivity.kt                 # Main activity
│   │   │   ├── ReminderActivity.kt             # Reminder activity
│   │   │   ├── navigation/
│   │   │   │   └── PillReminderNavigation.kt   # Navigation setup
│   │   │   ├── screens/
│   │   │   │   └── HomeScreen.kt               # Home screen
│   │   │   ├── components/                     # Reusable UI components
│   │   │   └── theme/                          # App theming
│   │   │       ├── Color.kt
│   │   │       ├── Theme.kt
│   │   │       └── Type.kt
│   │   ├── data/                               # Data layer
│   │   │   ├── local/                          # Local database
│   │   │   ├── remote/                         # Remote API
│   │   │   └── repository/                     # Repository pattern
│   │   ├── domain/                             # Domain layer
│   │   │   ├── model/                          # Domain models
│   │   │   └── usecase/                        # Use cases
│   │   ├── di/                                 # Dependency injection
│   │   ├── service/                            # Background services
│   │   └── utils/                              # Utility classes
│   ├── res/                                    # Resources
│   │   ├── layout/                             # Layout files
│   │   ├── values/                             # Strings, colors, themes
│   │   ├── drawable/                           # Drawable resources
│   │   └── mipmap-anydpi-v26/                  # App icons
│   └── AndroidManifest.xml                     # App manifest
├── build.gradle.kts                            # App-level build file
└── proguard-rules.pro                          # ProGuard rules
├── build.gradle.kts                            # Project-level build file
├── settings.gradle.kts                         # Project settings
├── gradle.properties                           # Gradle properties
└── gradle/wrapper/                             # Gradle wrapper
```

## Features (Planned)

### Phase 1 - Core Features
- [ ] Medication management (add/edit/delete)
- [ ] Basic reminder system
- [ ] Dose tracking

### Phase 2 - Enhanced Features
- [ ] Progress analytics
- [ ] User profiles
- [ ] Smart notifications

### Phase 3 - Advanced Features
- [ ] Healthcare integration
- [ ] Modern UI/UX
- [ ] Data sync & backup

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin
- **Database**: Room
- **Backend**: Firebase
- **Notifications**: Work Manager
- **Navigation**: Navigation Compose

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the app

## Development Status

🚧 **In Development** - Basic project structure created, ready for feature implementation.

## Next Steps

1. Implement data models
2. Set up Room database
3. Create medication management screens
4. Implement reminder system
5. Add notification functionality