# ⏰ ClockIn – Personal Task Manager Android App

---

## 🚀 Overview

**ClockIn** is a modern personal productivity Android application that helps users stay on top of their day with a focused, priority-driven task list.

Built with **classic Android Views + ViewBinding** and **Firebase** as the backend, the app is designed to be **simple, scalable, and production-ready**.

🎯 **Vision:**

> "Clock in to your day — track tasks, focus on what matters, and never miss a high-priority deadline."

---

## 📸 Screenshots

<div style="overflow-x:auto; padding:8px 2px;">
  <table>
    <tr>
      <td align="center"><img src="screenshots/splash_screen.png" alt="Splash Screen" width="200"/><br/><sub>Splash</sub></td>
      <td align="center"><img src="screenshots/login_screen.png" alt="Login Screen" width="200"/><br/><sub>Login</sub></td>
      <td align="center"><img src="screenshots/signup_screen.png" alt="Signup Screen" width="200"/><br/><sub>Signup</sub></td>
      <td align="center"><img src="screenshots/home_screen.png" alt="Home Screen" width="200"/><br/><sub>Home</sub></td>
      <td align="center"><img src="screenshots/add_task.png" alt="Add Task" width="200"/><br/><sub>Add Task</sub></td>
    </tr>
  </table>
</div>

---

## ✨ Key Features

### 🔐 Authentication & Startup Flow

- Firebase Auth (email/password login + signup)
- Cold-start Splash screen with one-shot routing (Tasks if logged in, else Login)
- Pure logout interceptor (`AuthStateListener`) that redirects to Login when the user becomes `null`
- Drawer locked on auth/splash flow, unlocked on main screens

### ✅ Task Management System

- Create, edit, delete, and complete tasks
- Due date + time with pre-formatted display label (e.g. "Today, 2:30 PM")
- Tags (`Urgent` / `Work` / `Personal`) and priority (`HIGH` / `NORMAL`)
- Checkbox toggle for completion with instant Firestore sync
- Empty-state, loading, and error (Snackbar) UI states

### 🎯 Priority-Based Task List

- Tasks split into **High Priority** and **Upcoming** sections
- Real-time Firestore query ordered by `dueDateTime`
- In-memory filtering of completed tasks in the ViewModel
- Sectioned `RecyclerView` with header + task rows via a single `TasksAdapter`

### 🧭 Navigation & App Shell

- Single-Activity + `DrawerLayout` architecture
- All screen changes go through a central `Navigator`
- Destinations: `Splash`, `Login`, `Signup`, `Tasks`, `About`
- Navigation drawer with Tasks / About top-level destinations
- Edge-to-edge layout with toolbar state managed per top fragment

### ⚡ Data & Performance

- Per-user Firestore subcollection: `users/{userId}/tasks/{taskId}`
- Defensive, schema-less model (`Task.fromData` / `toMap`, `SCHEMA_VERSION = 1`)
- Realtime updates via `callbackFlow` + `addSnapshotListener`
- Coroutines + `kotlinx-coroutines-play-services` for all Firebase calls
- Timber logging (never `android.util.Log`)

---

## 🛠️ Tech Stack

### 📱 Android Development

- Kotlin (100%)
- Android Views + ViewBinding
- Material Components (M2 – `Theme.MaterialComponents.*`)

### 🧠 Architecture

- MVVM (Fragment → ViewModel → Repository)
- Repository Pattern
- Single-Activity + custom `Navigator` / `Destination` navigation

### 🌐 Data Layer

- Firebase Auth, Cloud Firestore, Analytics (`firebase-bom`)
- Retrofit + Moshi + OkHttp (REST scaffolding via `TaskApi` / `TaskDto`)
- Firestore schema enforced in-app (`Task.fromData` coerces `Number` / `Timestamp` / `Date` → millis, `"true"` / `"false"` → `Boolean`)

### 🔧 Tooling

- AGP 9.3.1, Gradle 9.5.0 (Wrapper), JDK 21 (Build), Java 11 (Source/Target)
- minSdk 24, compileSdk/targetSdk 37
- Timber, Coroutines, ViewBinding, BuildConfig

---

## 📂 Project Architecture

```
com.narang.clockin
│
├── data/
│   ├── model/
│   │   └── Task.kt                  # Parcelable + fromData/toMap + SCHEMA_VERSION
│   ├── repository/
│   │   ├── TaskRepository.kt        # observe/add/update/delete/setCompleted
│   │   ├── FirestoreTaskRepository.kt # users/{uid}/tasks, orderBy("dueDateTime")
│   │   ├── AuthRepository.kt
│   │   └── FirebaseAuthRepository.kt
│   ├── TaskApi.kt / TaskDto.kt      # Retrofit REST scaffolding
│   └── AuthInterceptor.kt
│
├── domain/
│   ├── Task.kt / TaskRepository.kt / Result.kt
│   └── TaskRepositoryImp.kt
│
├── navigation/
│   ├── Destination.kt
│   ├── Destinations.kt              # Splash/Login/Signup/Tasks/About
│   └── Navigator.kt                 # navigate() + navigateToRoot()
│
├── ui/
│   ├── splash/
│   │   └── SplashFragment.kt
│   ├── auth/
│   │   ├── LoginFragment.kt
│   │   ├── SignupFragment.kt
│   │   ├── AuthViewModel.kt (+ Factory, UiState)
│   ├── tasks/
│   │   ├── TaskFragment.kt          # RecyclerView + FAB + menu dialog
│   │   ├── AddTaskDialogFragment.kt # create/edit sheet
│   │   ├── TaskViewModel.kt (+ Factory, UiState)
│   ├── adapter/
│   │   └── TaskAdapter.kt / TaskListItem.kt
│   └── about/
│       └── AboutFragment.kt
│
├── MainActivity.kt                  # Drawer + toolbar state + splash router + logout guard
├── ClockInApplication.kt            # Timber init
└── MainActivity layout (activity_main.xml, fragment_tasks.xml, drawer menu)
```

## 🧠 Architecture Flow

```
UI (Fragments + ViewBinding)
        ↓
ViewModel (StateFlow<TaskUiState> / AuthUiState)
        ↓
Repository Layer (FirestoreTaskRepository / FirebaseAuthRepository)
        ↓
Remote Data Source (Firestore users/{uid}/tasks, FirebaseAuth)
        ↓
UI State Updates (render() + Snackbar / empty / loading states)
```

## 🎯 Core Modules

### 🏠 Tasks Module

- Priority-grouped task browsing system
- Add/edit dialog with date-time picker, tag, and priority
- Overflow menu (edit/delete) per task row

### 🔎 Auth Module

- Login/signup with form validation and error states
- Auth ViewModel backed by `FirebaseAuthRepository`
- Automatic session restore on cold start

### ℹ️ About Module

- Static info screen reachable from the drawer
- Version + app description

---

## ⚙️ Setup Instructions

### 1. Clone the Repository

```
git clone <your-repo-url>
cd ClockIn
```

### 2. Open in Android Studio

- Open the project folder
- Use JDK 21 (Gradle toolchain is managed)
- Wait for Gradle sync (Gradle Wrapper 9.5.0, AGP 9.3.1)

### 3. Add Firebase Config (required, gitignored)

This project uses Firebase Auth + Firestore. `google-services.json` and `local.properties` are gitignored and never committed.

- Create a Firebase project
- Add an Android app with ID `com.narang.clockin`
- Download `google-services.json` into `app/`:
  ```
  app/google-services.json
  ```
- Enable **Authentication → Email/Password** and **Cloud Firestore** in the Firebase console

### 4. Build & Run

```
./gradlew assembleDebug
```

Useful commands:

```
./gradlew testDebugUnitTest          # JVM unit tests (incl. Task.fromData/toMap)
./gradlew connectedDebugAndroidTest  # instrumented tests
./gradlew lint                       # Android lint
./gradlew check                      # full check
```

---

## 📈 Future Enhancements

- 🔔 Due-date reminders / notifications
- 🔍 Search, tag filter, and sort options
- 📶 Offline persistence + sync status indicator
- ⭐ Recurring tasks and subtasks
- 📊 Productivity stats (completed per day/week)
- 🌙 Dark theme + dynamic color support
- 🧩 Home-screen widget for high-priority tasks

---

## 🏆 Project Highlights

- ✔ Production-style single-Activity + Navigator navigation
- ✔ Realtime per-user Firestore task sync
- ✔ Defensive schema-less model (old/new doc versions coexist)
- ✔ No compound index needed (single-field `orderBy`, in-memory filter)
- ✔ Clean MVVM + Repository separation, ViewBinding-only views
- ✔ Unit-tested model layer (`TaskFromDocumentTest`)

---

## 👨‍💻 Developer

**ClockIn** – Android (Kotlin, Views, Firebase) task manager.

---

## 📌 License

This project is for educational and portfolio purposes only.

---

## 🚀 Final Note

ClockIn is a focused, Firebase-backed productivity app concept designed to grow into a full daily-planning product with reminders, offline-first sync, and smart prioritization.
