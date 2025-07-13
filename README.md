# Duress Stepss - Step Counter App

This is an Android application built to track a user's steps using the device's step counter sensor. It stores the data every minute, and displays it in a clean, modern UI built entirely with Jetpack Compose.

The project was designed to not only meet the core requirements but also to showcase best practices, including a scalable architecture, a modern technology stack, and comprehensive tests.

## Features

-   **Real-time Step Tracking**: Utilizes the `TYPE_STEP_COUNTER` sensor for accurate, battery-efficient step detection.
-   **Jetpack Compose UI**: The entire user interface is built with Jetpack Compose, featuring a clean, reactive display of the current step count.
-   **Coroutine-based Asynchronous Operations**: All sensor handling and database operations are performed on background threads using Kotlin Coroutines and Flow, preventing any impact on UI performance.
-   **1-Minute Data Persistence**: A background task saves the current step count to a local Room database every minute.
-   **Optional Reset Functionality**: Includes a "Reset" button for testing and demonstration purposes.

## Architecture:

A key architectural decision was to structure the application using a **package-by-feature** approach.

-   **`stepcounter` Feature Package**: This is a self-contained directory that holds all UI, domain, and data logic specific to the step counting feature. This makes the feature easy to understand, maintain, and test in isolation. If a new feature (e.g., "user profile" or "settings") were added, it would get its own package, minimizing dependencies and cognitive load.
-   **`core` Package**: This is a shared directory containing code that would be used by multiple features in a larger app. This includes the Room database definition, DAOs, Hilt dependency injection modules, and shared utility classes (like `TestTags`). This structure prevents code duplication and promotes consistency.

This architecture demonstrates a forward-thinking approach, building for a future where the app might grow in complexity.

## Technology Stack

-   **UI**:[Jetpack Compose] with a single-activity architecture.
-   **Asynchronicity**: [Kotlin Coroutines] and [Flow]for all background work, ensuring a responsive UI and efficient resource management.
-   **Dependency Injection**: [Hilt] for robust, boilerplate-free dependency management.
-   **Persistence**: [Room Database] for storing structured, time-series data.

## Testing Strategy

The project includes a multi-layered and comprehensive testing strategy to ensure code quality and correctness:

-   **Unit Tests (JVM)**: Located in `app/src/test`, these validate the logic of individual classes (ViewModel, Use Cases) in isolation.
-   **Integration Tests (Android)**: Located in `app/src/androidTest`, these verify the interaction between components, specifically testing the Room `StepCountDao` to ensure database operations are flawless.
-   **UI Tests (Android)**: Also in `app/src/androidTest`, these confirm that the UI correctly reflects the state from the ViewModel and that user interactions work as expected. We use `testTag`s to identify UI components, which is a best practice for creating robust and maintainable UI tests.

## Setup and Running the App

1.  **Clone the repository:**
2.  **Open the project** in Android Studio.
3.  **Build the project:** Let Gradle sync and download all the required dependencies.
4.  **Run the app:**
    -   **On a real device:** This is the recommended approach, as most modern physical devices have a step counter sensor.
    -   **On an emulator:** Many Android emulators **do not** have a virtual step counter sensor. If you run the app on an emulator without this sensor, the app will display a "Sensor not available" message. This is the expected behavior.

## Limitations and Assumptions

-   **Sensor Availability**: The app relies exclusively on the `TYPE_STEP_COUNTER` sensor. It does not attempt to fall back to the accelerometer or other sensors to derive step counts, as per the challenge requirements.
-   **Emulator Support**: As noted above, the app's core functionality cannot be tested on an emulator that lacks a virtual step counter sensor.
-   **Background Operation**: The app persists data every minute while it is open. For an app that needs to track steps when the app is not open, a `ForegroundService` would be implemented to ensure the sensor listener runs continuously, but that was deemed outside the scope of this task.
-   **Data Retrieval**: The requirements specified that data should be persisted to the database, but did not include any features for displaying or using that historical data in the UI. Therefore, the app does not load the persisted data back into the UI. The data is saved as required, and the database integration is verified via integration tests.

## Screenshots

### Main Screen
![Sample UI](/docs/images/stepscounted.png)

### Unit Tests
![Unit Test Results](/docs/images/unittests.png)

### Integration Tests
![Integration Test Results](/docs/images/integrationtest.png)

### UI Tests
![UI Test Results](/docs/images/uitests.png)