# Echo - Focus & Productivity App

Welcome to Echo! This modern productivity application is designed to help you stay focused, track your energy levels, and overcome procrastination. Built with Kotlin Multiplatform and Jetpack Compose, Echo works seamlessly across Android and Desktop platforms.

Echo is an open-source project that aims to improve your productivity and well-being.

## Features

- **Focus Mode**: Eliminate distractions with a dedicated focus environment
- **Pomodoro Timer**: Use time-blocking techniques to improve productivity
- **Energy Tracking**: Monitor your mood and energy levels throughout the day
- **Procrastination Helper**: Get motivation and practical tips when you need a boost
- **Breathing Exercises**: Quick relaxation techniques to reduce stress
- **AI Chat Assistant**: Get personalized productivity advice and answers to your questions

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or newer

### Running the App
1. Clone this repository:
   ```
   git clone https://github.com/itza2k/Echo.git
   cd Echo
   ```
2. Open the project in Android Studio
3. Select your target device (Android or Desktop)
4. Click Run

## Project Structure

This is a Kotlin Multiplatform project targeting Android and Desktop platforms.

* `/composeApp` contains the shared code for all platforms:
  - `commonMain` has the core application logic and UI
  - `androidMain` contains Android-specific code
  - `desktopMain` contains Desktop-specific code

## Technologies

- **Kotlin Multiplatform**: Share code between platforms
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Beautiful and consistent design system
- **SQLDelight**: Type-safe database access
- **Anthropic Claude**: AI language model for intelligent chat responses
- **Google Gemini**: Advanced AI model for natural language processing
- **Ktor**: Kotlin framework for handling API requests to AI services

## Contributing

We welcome contributions to Echo! If you'd like to contribute, please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
