# Walk Reward Application

## Description
The Walk Reward Application is designed to incentivize walking by tracking the user's steps, distance, and time, and awarding points for their physical activity. The application includes features such as real-time sensor data tracking, Google Maps integration to display the user's path, and an interactive user interface for viewing rewards and profile information.

## Usage
1. Grant the necessary permissions for location and sensors.
2. Start the walking activity by clicking the "Start" button.
3. Monitor real-time updates on steps, distance, time, and speed.
4. Stop the walking activity by clicking the "Stop" button.
5. View the confirmation dialog with accumulated steps, time, distance, and reward points.
6. Save the activity to update the profile with the new values.

## Functionality
- **Real-time Data Tracking**: Tracks time, distance, steps, and speed in real-time when the user is walking.
- **Google Maps Integration**: Displays the user's current location and path walked using Google Maps.
- **Reward System**: Users earn reward points based on their walking activity, which can be redeemed for coupons.
- **Profile Management**: Allows users to view and update their profile, including changing their username and profile picture.
- **Sensor Data**: Uses the device's sensors to count steps and track movement.
- **Interactive Map**: Displays the user's path and current location on a Google Map.
- **Step Counting**: Utilizes the device's step sensor to count steps in real-time.
- **Reward Points Calculation**: Calculates reward points based on time walked, distance covered, and steps taken.
- **Profile Picture Selection**: Allows users to select a profile picture from their device's gallery.
- **Live Data Overlay**: Displays live data (time, distance, speed, steps) while walking.
- **Compass Functionality**: Integrates a digital compass to show the user's current direction, using the device's rotation vector sensor.

## Features
- **Real-time Data Tracking**: Tracks time, distance, steps, and speed in real-time when the user is walking.
- **Google Maps Integration**: Displays the user's current location and path walked using Google Maps.
- **Reward System**: Users earn reward points based on their walking activity, which can be redeemed for coupons.
- **Profile Management**: Allows users to view and update their profile, including changing their username and profile picture.
- **Sensor Data**: Uses the device's sensors to count steps and track movement.
- **Interactive Map**: Displays the user's path and current location on a Google Map.
- **Step Counting**: Utilizes the device's step sensor to count steps in real-time.
- **Reward Points Calculation**: Calculates reward points based on time walked, distance covered, and steps taken.
- **Profile Picture Selection**: Allows users to select a profile picture from their device's gallery.
- **Live Data Overlay**: Displays live data (time, distance, speed, steps) while walking.
- **Compass Functionality**: Integrates a digital compass to show the user's current direction, using the device's rotation vector sensor.

## Development
- **Duration**: This project took approximately 50 hours over the span of a week to complete.
- **Challenges**:
  - Integrating Google Maps and displaying the user's path in real-time.
  - Handling sensor data to accurately track steps and movement.
  - Calculating distance in real-time and updating the UI with live data.
  - Managing the state of the application to ensure smooth transitions between walking and resting states.
  - Ensuring the application is responsive and user-friendly across different devices and screen sizes.

## Technologies Used
- **Android Studio**: IDE for Android application development.
- **Kotlin**: Primary programming language.
- **Google Maps SDK**: For map integration and location tracking.
- **Google Play Services Location**: For accessing location data.
- **LiveData and ViewModel**: For managing UI-related data in a lifecycle-conscious way.
- **SensorManager**: For accessing and managing device sensors.

## Challenge
- Implement the `updateSteps` function to correctly update the steps during the walk and total steps.
  - **ViewModel Initialization**: Ensure the ViewModel is correctly initialized in the `HomeFragment`.
  - **Setup UI and Observers**: Set up the UI components and observers to monitor LiveData changes.
  - **Sensor Event Listener**
  - **Confirmation Dialog Update**
- Implement the `MutableLiveData` objects in the `HomeViewModel` to update the steps and total steps.
  - **LiveData Initialization**: Initialize the LiveData objects in the ViewModel.
  - **LiveData Update**: Update the LiveData objects when the sensor data changes.
  - **LiveData Observers**: Observe the LiveData objects in the Fragment.

## Citations and Resources
- [Google Maps SDK Documentation](https://developers.google.com/maps/documentation/android-sdk/overview)
- [Google Play Services Location Documentation](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary)
- [Android Official Documentation](https://developer.android.com/docs)
- Stack Overflow for troubleshooting specific issues related to Android development and sensor integration.
- Some debugging and code comments are from ChatGPT helps

## Installation
To run this application, clone the repository and import it into Android Studio. Build the project using Gradle and run it on an Android device or emulator. Make sure to add your Google Maps API key in the `AndroidManifest.xml` file.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
