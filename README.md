# Notification Scheduler

A modern Android application built with Kotlin and Jetpack Compose that allows users to schedule notifications with custom images at specific dates and times.

## Features

- 📅 Schedule notifications with custom date and time
- 🖼️ Add custom images to notifications
- 📝 Edit existing notifications
- 🗑️ Delete scheduled notifications
- 🌍 Multilingual support (English, Turkish, German, French)
- 🎨 Material 3 Design with custom yellow theme
- 📱 Modern Android architecture

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Dependencies**:
  - Room Database for local storage
  - WorkManager for scheduling notifications
  - Coil for image loading
  - Material 3 components
  - AndroidX libraries

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern and clean architecture principles:

- **Data Layer**: Room Database, Entities, DAOs
- **Domain Layer**: Repository pattern
- **UI Layer**: Compose UI, ViewModels
- **Background Processing**: WorkManager for notification scheduling

## Screenshots

[Add your screenshots here]

## Requirements

- Android Studio Arctic Fox or newer
- Minimum SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- Kotlin 1.8.0 or newer

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/notification-scheduler.git
```

2. Open the project in Android Studio

3. Build and run the application

## Features in Detail

### Notification Scheduling
- Select specific date and time
- Add title and description
- Optional custom image attachment
- Automatic cleanup of expired notifications

### Notification Management
- View all scheduled notifications
- Edit existing notifications
- Delete notifications
- Sort notifications by date

### UI/UX
- Material 3 design system
- Custom yellow theme
- Responsive layout
- Intuitive user interface
- Native date and time pickers

### Localization
Supports multiple languages:
- English (default)
- Turkish
- German
- French

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

- Material Design 3 Guidelines
- Android Jetpack libraries
- Kotlin Coroutines
- WorkManager for background processing

## Contact

Your Name - [@yourusername](https://twitter.com/yourusername)

Project Link: [https://github.com/yourusername/notification-scheduler](https://github.com/yourusername/notification-scheduler) 