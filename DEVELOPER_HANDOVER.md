# CureMe Android Application - Developer Handover Documentation

This document serves as a comprehensive developer handover guide for the **CureMe Android Application** codebase. It outlines the project's purpose, architecture, features, tech stack, configuration, and codebase guidelines to help a new developer quickly onboard and maintain the project.

---

## 📱 Project Overview

**CureMe** is a modern health tracking and AI-driven medical consultation companion application. It provides users with automated diagnostic guidance, health reports analysis, schedule/appointment management, and profiles for multiple family members under a single account.

### Core Value Propositions:
1. **AI Medical Chat (GPT-powered)**: Converse with virtual assistants (General, Creative, Technical, Academic) and switch between standard normal chats and structured medical **Case Chats**.
2. **AI-driven Health Reports**: Analyze health reports (blood tests, X-rays, prescriptions), view detailed summary cards, download reports as PDFs, and obtain priority/severity ratings (High, Attention, Normal) with automatic empty checks.
3. **Appointment Scheduling**: Schedule and reschedule health/doctor appointments for patients (self or family members).
4. **Family Profiles**: Manage separate health logs and profiles for family members.

---

## 🏛 Architecture & Codebase Structure

The project follows the standard **MVVM (Model-View-ViewModel)** architectural pattern coupled with Jetpack Compose for the UI layer and clean repositories for data access.

### Module Structure
The project is structured as a single-module Android application (`app`), with package-based feature modularization inside:

```
com.bussiness.curemegptapp/
│
├── activity/               # Entry points (e.g. MainActivity)
├── apiendpoint/            # Retrofit Endpoint wrappers/routes
├── apimodel/               # Data Transfer Objects (DTO) for Network API calls
├── base/                   # Base classes for ViewModels and Activities
├── context/                # Application Context helpers
├── data/                   # Local databases, Session management, local data entities
├── di/                     # Dependency Injection modules (Dagger Hilt)
│   ├── ApiService.kt       # Retrofit REST API interface definitions
│   └── NetworkModule.kt    # OkHttpClient, Retrofit, and Repository DI configuration
├── fcm/                    # Firebase Cloud Messaging handling for push notifications
├── navigation/             # Type-safe Navigation Graph definitions using Kotlinx Serialization
│   ├── AppDestination.kt   # Sealed class navigation destinations
│   ├── MainNavGraph.kt     # App core feature flow navigation graph
│   └── AppNavGraph.kt      # Main host navigation entry controller
├── repository/             # Repository implementations (Remote & Local abstraction layer)
│   ├── Repository.kt       # API Repository interfaces
│   └── RepositoryImpl.kt   # Network calls execution with Flow<NetworkResult>
├── ui/                     # UI components, Dialogs, Bottom Sheets, and Compose Screens
│   ├── component/          # Reusable Compose widgets (buttons, headers, inputs)
│   ├── dialog/             # Popups (Delete dialog, share dialog, confirm dialogs)
│   ├── screen/             # Actual full-screen UI views (split into auth, intro, main, settings)
│   ├── sheet/              # Bottom Sheets (filters, details sheets)
│   └── theme/              # Custom design tokens, typography (Urbanist), colors, shapes
├── util/                   # Common helper utilities (SessionManager, downloaders, formatting)
└── viewmodel/              # Architecture ViewModels orchestrating state & UI events
```

---

## ⚙️ Tech Stack & Dependencies

The application is built using modern Android development practices and libraries:

| Layer | Technology / Library | Usage |
| :--- | :--- | :--- |
| **UI** | Jetpack Compose (Material 3) | Declarative UI framework with modern design tokens |
| **DI** | Dagger Hilt | Dependency injection for modularized dependencies |
| **Network** | Retrofit + OkHttp | HTTP Client for API communication, including logging & custom headers |
| **Serialization** | Kotlinx Serialization | Type-safe JSON serialization & Compose route parameters |
| **Asynchrony** | Kotlin Coroutines & Flow | Asynchronous database and network operations |
| **Image Loading** | Coil | Memory-efficient asynchronous image loading |
| **Animations** | Lottie Compose | Rich, vector-based interactive micro-animations |
| **Utilities** | Timber | Structured and level-based logging |
| **Image Cropping** | Vanniktech Image Cropper | Profile image cropping utility |
| **Notifications** | Firebase Cloud Messaging (FCM) | Server-driven push notifications |
| **Auth** | Play Services Auth | Google Auth Integration |

---

## 🌐 Network Configuration & Session Security

All network calls are mediated by Retrofit and customized via `OkHttpClient` interceptors:
- **Logging Interceptor**: Enabled in debug builds to output request and response payloads to Logcat (`RetrofitLog` and `API` tags).
- **Session Event Bus / Interceptor**: Automatically listens for HTTP `401 Unauthorized` responses. If a session expires, it calls `SessionEventBus.emitSessionExpired()`, forcing an automatic user logout and redirecting them safely to the authentication screen.
- **Base URL Setting**: Configured dynamically via `local.properties`:
  ```properties
  baseUrl="https://curemegpt.tgastaging.com/api/"
  ```
  If not found, it defaults to the staging URL in `app/build.gradle.kts`.

---

## 📁 Key Source Files to Know

1. **[MainNavGraph.kt](file:///d:/CureMeFinalRelease/Cureme-Android/app/src/main/java/com/bussiness/curemegptapp/navigation/MainNavGraph.kt)**:
   Specifies the Compose Navigation graph containing routes for dashboard screens, family tracking, chat panels, and reports views.
2. **[ChatDataScreen.kt](file:///d:/CureMeFinalRelease/Cureme-Android/app/src/main/java/com/bussiness/curemegptapp/ui/screen/main/chat/ChatDataScreen.kt)**:
   Core layout representing active medical consultation chat. Handles normal and case chat states, attachments UI, deletion dialogues, and popup switch actions.
3. **[ChatDataViewModel.kt](file:///d:/CureMeFinalRelease/Cureme-Android/app/src/main/java/com/bussiness/curemegptapp/ui/viewModel/main/ChatDataViewModel.kt)**:
   Manages the state representation of chat data (messages, images, PDF streams, audio recordings). Contains `clearMessages()` to wipe message buffers during switch logic.
4. **[ReportScreen.kt](file:///d:/CureMeFinalRelease/Cureme-Android/app/src/main/java/com/bussiness/curemegptapp/ui/screen/main/reports/ReportScreen.kt)**:
   Individual view for details of a uploaded diagnostic report. It analyzes symptoms, parses severity, and hides tags gracefully when empty.
5. **[ReportCard.kt](file:///d:/CureMeFinalRelease/Cureme-Android/app/src/main/java/com/bussiness/curemegptapp/ui/screen/main/healthReports/ReportCard.kt)**:
   Individual list item component for diagnostic reports listing. Dynamically checks and renders priority tags when severity text is present.
6. **[CustomPowerSpinner.kt](file:///d:/CureMeFinalRelease/Cureme-Android/app/src/main/java/com/bussiness/curemegptapp/ui/component/input/CustomPowerSpinner.kt)**:
   Re-usable custom drop-down spinner. Modified to utilize the `Urbanist` font family universally across all selections (e.g. member select in filters).

---

## 🛠 Handover Notes & Codebase Practices

New developers should adhere to the following coding conventions already established in the codebase:
- **Dynamic Font Family**: Ensure UI Text composables specify `fontFamily = FontFamily(Font(R.font.urbanist_regular))` (or relevant weights) to match the brand identity.
- **Null Safety in Severity Tags**: Always ensure checks like `isNotBlank()` are performed before showing custom tags (`PriorityImageTag`) to prevent empty containers from displaying default background colors.
- **ViewModel UI States**: Avoid modifying UI state directly inside Compositions. Always dispatch actions to ViewModels via callbacks, and expose state variables through `MutableStateFlow` collected with lifecycle scope (e.g. `collectAsStateWithLifecycle()` or `collectAsState()`).
- **Resource Management**: Strings, dimensions, colors, and graphics must be stored inside their respective Android resources folders (`strings.xml`, `colors.xml`, `R.font`, `R.drawable`) to facilitate localization and consistency.
