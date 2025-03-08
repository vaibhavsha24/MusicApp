# Music Player App 🎵

## Overview  
This is a **Android Music Player app** built using **Jetpack Compose** and follows the **MVVM (Model-View-ViewModel) architecture**. It supports **background music playback**, a **mini-player (PiP mode)**, and **offline caching** for seamless user experience.  

---

## 📌 Features  
- **MVVM Architecture** for clean separation of concerns.  
- **Jetpack Compose** for a fully declarative UI.  
- **ExoPlayer** for smooth audio playback.  
- **Hilt Dependency Injection** for efficient dependency management.  
- **Foreground Service** for background music playback.  
- **Picture-in-Picture (PiP) Mode** with media controls.  
- **Local Caching (SharedPreferences)** to store last fetched songs in Case of Network failure Songs are loaded from Cache.  
- **Network Handling** with Retrofit for API calls.  
- **Custom UI Components** like a scrolling song carousel and interactive playback controls.  

---

## 📚 Project Structure  

### 1 **Presentation Layer (UI & ViewModel)**  
   - Built using **Jetpack Compose** for UI, ensuring a modern and declarative design.  
   - **ViewModel (Jetpack)** is used to store UI state and handle business logic. Used HiltViewmodel to use DI for the Repository instance
   - **LiveData & StateFlow** are used for managing state updates reactively.
   - UI handled for the **PIP Mode* and actions added.
   - Enabled the change of songs based on Scrolling of Songs.
   - **ExoPlayer** is used to play, pause, and manage song playback efficiently.  
     
### 2 **Data Layer (Repository & Data Handling)**  
   - **Repository Pattern** is used to handle data from API.  
   - **Hilt Dependency Injection** ensures modular, testable, and reusable components.  

### 3 **Service Layer (Foreground Service & PiP Mode)**  
   - **Foreground Service** keeps the music player running in the background.  
   - **Picture-in-Picture (PiP) Mode** allows users to control music when the app is minimized.  
   - **BroadcastReceiver** is used to handle media actions like play, pause, and skip.  

### 4 **Network Layer (API Handling)**  
   - **Retrofit** is used for handling API calls with proper error handling.
   - **Single Point** in the app to make api calls to make it extendable and reusable.
   - **DataState Wrapper** used on basis of Response i.e DataState.Success, DataState.Error and DataState.Loading
   - Used **Dispatcher** to call API on BG Thread 


---

## 🛠️ Technologies Used  
| Technology | Purpose |
|------------|---------|
| **Jetpack Compose** | UI Development |
| **MVVM** | Architecture Pattern |
| **ExoPlayer** | Audio Playback |
| **Hilt DI** | Dependency Injection |
| **Retrofit** | API Handling |
| **StateFlow & LiveData** | State Management |
| **Foreground Service** | Background Music Playback |
| **SharedPreferences** | Offline Caching |
| **Picture-in-Picture Mode** | Mini Player |

---

## 🛠️ Setup & Installation  
1. Clone the repository:  
   ```sh
   git clone https://github.com/vaibhavsha24/MusicApp.git
   ```
2. Open the project in **Android Studio**.  
3. Sync the Gradle files and install dependencies.  
4. Run the app on an emulator or a physical device.  

