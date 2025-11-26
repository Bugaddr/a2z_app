# Copilot Instructions for a2z_app

## Project Overview
This is a Kotlin-based Android application that functions primarily as a WebView wrapper for an external web application (`https://jrchintu.github.io/a2z_old_sheet`).

## Architecture & Core Components
- **Single Activity:** The app uses a single `MainActivity` architecture.
- **WebView Wrapper:** The core logic is in `MainActivity.kt`, which initializes and configures a `WebView`.
- **Manifest:** `AndroidManifest.xml` handles permissions (`INTERNET`) and hardware acceleration.

## Key Patterns & Conventions

### WebView Configuration
- **Performance:** JavaScript, DOM Storage, and Database are enabled.
- **Caching:** Uses `WebSettings.LOAD_CACHE_ELSE_NETWORK` to prioritize cached content.
- **Cookies:** Explicitly enables third-party cookies via `CookieManager`.
- **Navigation:** Overrides `onBackPressed` to handle WebView history navigation before exiting the app.

### UI & Display
- **High Refresh Rate:** `MainActivity` explicitly requests a 120Hz refresh rate on supported devices (Android R+).
- **Layouts:** XML-based layouts in `src/main/res/layout`.
- **ViewBinding:** Enabled in `build.gradle` (`buildFeatures { viewBinding true }`).
  - *Note:* Legacy code may use `findViewById`, but prefer ViewBinding for new implementations.

### Build & Dependencies
- **SDK:** Min SDK 24, Target SDK 33.
- **Language:** Kotlin 1.8.
- **Build System:** Gradle. Use `./gradlew assembleDebug` to build.

## Developer Workflow
- **Running:** Standard Android launch configuration.
- **Debugging:** Use standard Android Studio/ADB debugging tools.
- **Modifying WebView:** When changing WebView settings, ensure `CookieManager` and `WebSettings` are updated in `MainActivity.kt`.

## Critical Files
- `a2z_app/src/main/java/com/example/a2z_app/MainActivity.kt`: Main entry point and WebView configuration.
- `a2z_app/src/main/AndroidManifest.xml`: App permissions and activity declarations.
- `a2z_app/build.gradle`: Module-level build configuration.
