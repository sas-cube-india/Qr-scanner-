# QR Code Scanner & Generator

This is a modern Android mobile app that allows users to scan and generate QR codes with a rich set of features. The app is built with Kotlin and follows the latest Material Design guidelines.

## Features

*   **QR Scanner**
    *   Scan from camera and gallery.
    *   Support for multiple formats (Text, URL, Contact, Email, Wi-Fi, Events, etc.).
    *   Batch/bulk scanning (Pro version).
*   **QR Generator**
    *   Generate QR codes for text, URL, contact, email, Wi-Fi, etc.
    *   Save and share generated QR codes.
*   **History & Favorites**
    *   Save all scanned QR codes with a timestamp.
    *   Mark scans as favorites for quick access.
*   **Monetization & Ads**
    *   AdMob integration: banner and interstitial ads.
    *   Pro version purchase via Google Play Billing to remove ads and unlock bulk scanning.
*   **Authentication**
    *   Sign in with Firebase + Google Authentication.
*   **UI/UX**
    *   Modern, clean, and minimalistic HiFi UI.
    *   Uses the Poppins font.
    *   Custom color palette.
    *   Smooth animations and responsive layouts.
    *   Splash screen.

## Project Structure

The project follows a standard Android project structure. Here are some of the key directories and files:

*   `app/src/main/java/com/example/qrcodescanner/`: The root package for the app's source code.
    *   `data/`: Contains the Room database components (Entity, DAO, Database).
    *   `ui/`: Contains the UI components (Fragments, ViewModels, Adapters).
    *   `MainActivity.kt`: The main activity of the app.
*   `app/src/main/res/`: The resource directory.
    *   `layout/`: XML layout files for activities and fragments.
    *   `drawable/`: Vector drawables for icons.
    *   `values/`: Resource files for strings, colors, styles, and themes.
    *   `menu/`: XML files for navigation menus.
    *   `font/`: Font files (Poppins).
    *   `anim/`: Animation files.
    *   `xml/`: XML files for general-purpose resources (e.g., `file_paths.xml` for `FileProvider`).
*   `build.gradle`: The project-level Gradle build file.
*   `app/build.gradle`: The app-level Gradle build file, where all dependencies are declared.

## Getting Started

### Prerequisites

*   Android Studio Arctic Fox | 2020.3.1 or later.
*   Java Development Kit (JDK) 11 or later.
*   An Android device or emulator running API level 21 or higher.

### Installation

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/your-username/qr-code-scanner.git
    ```
2.  **Open the project in Android Studio:**
    *   Open Android Studio.
    *   Click on "Open an Existing Project".
    *   Navigate to the cloned repository and select the root directory.
    *   Android Studio will automatically sync the Gradle project.

## Configuration

Before you can build and run the app, you need to configure a few things.

### 1. Firebase

1.  Go to the [Firebase console](https://console.firebase.google.com/).
2.  Create a new Firebase project.
3.  Add an Android app to your Firebase project with the package name `com.example.qrcodescanner`.
4.  Download the `google-services.json` file and place it in the `app/` directory of the project.
5.  In the Firebase console, go to **Authentication** and enable the **Google** sign-in provider.

### 2. Google Sign-In

1.  When you enable Google Sign-In in the Firebase console, it will provide you with a **Web client ID**.
2.  Open `app/src/main/res/values/strings.xml` and replace `YOUR_WEB_CLIENT_ID` with your actual Web client ID.

### 3. AdMob

1.  Go to the [AdMob console](https://apps.admob.com/).
2.  Create a new AdMob app.
3.  You will get an **App ID**.
4.  Open `app/src/main/AndroidManifest.xml` and add the following `<meta-data>` tag inside the `<application>` tag, replacing `YOUR_ADMOB_APP_ID` with your actual App ID.
    ```xml
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="YOUR_ADMOB_APP_ID"/>
    ```
5.  The project uses test ad unit IDs. To use your own ads, replace the ad unit IDs in `activity_main.xml` and `MainActivity.kt` with your own.

### 4. Google Play Billing

1.  In the [Google Play Console](https://play.google.com/console/), set up your in-app products.
2.  In `MainActivity.kt`, replace the placeholder product ID `"pro_version"` with your actual product ID.

### 5. FileProvider

The app uses a `FileProvider` to share generated QR codes. The authorities for the `FileProvider` are generated dynamically using the `applicationId`. You need to add the following `<provider>` tag inside the `<application>` tag in `app/src/main/AndroidManifest.xml`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

## Development

### Architecture

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern, which is recommended by Google.

*   **Model:** The data layer, implemented with Room database.
*   **View:** The UI layer, implemented with Activities and Fragments.
*   **ViewModel:** The business logic layer, which exposes data to the View and handles user interactions.

### Libraries and Technologies

*   **Kotlin:** The primary programming language.
*   **Android Jetpack:**
    *   **View Binding:** To easily access views from XML layouts.
    *   **LiveData:** To build data objects that notify views when the underlying database changes.
    *   **ViewModel:** To store and manage UI-related data in a lifecycle-conscious way.
    *   **Room:** For local database storage.
    *   **CameraX:** To implement the camera functionality for the QR scanner.
    *   **SplashScreen API:** For the splash screen.
*   **ML Kit:** For QR code detection.
*   **ZXing ("Zebra Crossing"):** For QR code generation.
*   **Material Components for Android:** For modern UI components.
*   **Firebase Authentication:** For user authentication.
*   **Google Play Billing Library:** For in-app purchases.
*   **AdMob:** For ads.

## How to Run

1.  Make sure you have completed all the steps in the **Configuration** section.
2.  Connect an Android device to your computer or start an emulator.
3.  In Android Studio, click on the **Run** button (or press `Shift` + `F10`).

## Deployment

To deploy the app to the Google Play Store, you need to generate a signed APK or App Bundle.

1.  Go to **Build > Generate Signed Bundle / APK...**.
2.  Follow the on-screen instructions to create a new keystore or use an existing one.
3.  Choose the build type (APK or App Bundle) and the flavor (if any).
4.  Once the build is complete, you can upload the generated file to the Google Play Console.

## Operation Guide

*   **Scanning:**
    *   The app opens on the "Scanner" screen.
    *   Point your camera at a QR code to scan it automatically.
    *   Tap "Scan from Gallery" to pick an image with a QR code from your phone.
    *   A dialog will appear with the scanned data. You can copy the data or close the dialog.
*   **Generating:**
    *   Navigate to the "Generator" screen.
    *   Enter the text you want to encode in the input field and tap "Generate".
    *   The generated QR code will be displayed.
    *   You can save the QR code to your gallery or share it with other apps.
*   **History:**
    *   Navigate to the "History" screen to see a list of all your scanned QR codes.
    *   Tap the heart icon to mark a scan as a favorite.
*   **Favorites:**
    *   Navigate to the "Favorites" screen to see a list of your favorite scans.
*   **Settings:**
    *   Navigate to the "Settings" screen to sign in with your Google account or to purchase the Pro version.
