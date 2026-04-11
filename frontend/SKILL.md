# 📱 FRONTEND_SKILL.md - Android App Manual

This document provides the architectural rules, module structure, and coding conventions for the Android mobile application. The frontend is built natively using **Java**, follows a **Multi-Module MVVM** (Model-View-ViewModel) architecture, and utilizes an **Offline-First** mindset.

---

## 1. Architecture Pattern (MVVM & Offline-First)
The app strictly follows the MVVM architecture combined with an Offline-First data flow.

**Flow:** `UI (Fragment)` ➡️ `ViewModel` ➡️ `Repository (:core:data)` ➡️ `Local DB (Room)` / `Remote API (:core:network)`

* **Offline-First Mindset:** 1. The UI **only** observes the Local Database (Room) via `LiveData`.
  2. A background job or Repository calls the backend API to fetch new data.
  3. New data is saved directly into Room.
  4. Room automatically triggers a `LiveData` update, and the UI re-renders.
* **UI Layer:** Responsible only for rendering data and capturing user events. **No business logic here.**
* **ViewModel Layer:** Holds UI state. Survives configuration changes.
* **Repository Layer:** The single source of truth for data.

---

## 2. Multi-Module Architecture Guide
The application is heavily modularized to separate concerns. Code must be placed in the correct module.

### 📦 `:app` (The Shell)
* **Purpose:** Ties everything together. Contains the `Application` class, `MainActivity`, Firebase Cloud Messaging (`FCM`), and centralized Dependency Injection (`NavigationModule`).
* **Rule:** Features should not be built here.

### 🛠️ `:core:*` (Foundational Modules)
These modules provide shared data, models, and utilities to the feature modules.
* **`:core:common`**: Utility classes (`DateUtils`, `TimeUtils`, `Result` wrapper).
* **`:core:data`**: The Single Source of Truth. Contains Room `AppDatabase`, `DAOs`, `Entities`, and all `Repository` implementations.
* **`:core:model`**: Pure data models (POJOs/DTOs) shared across the app (e.g., `Post`, `Record`, `RoutePoint`).
* **`:core:navigation`**: Interfaces for inter-module navigation (`AppNavigator`) so feature modules don't depend directly on each other.
* **`:core:network`**: Retrofit `ApiServices`, network payloads/responses, and network data sources.
* **`:core:service`**: Foreground services (e.g., `LocationTrackingService`).
* **`:core:system`**: The central UI library. Contains shared UI components (`Button`, `DatePickerHelper`), generic UI layouts (`layout_loading_state`, `layout_top_app_bar`), base themes (`themes.xml`, `styles.xml`), shared colors (`colors.xml`), and common drawables/icons. 

### 🚀 `:feature:*` (UI Modules)
These modules contain the Fragments, ViewModels, and Adapters for specific business domains. They depend on `:core` modules.
* **`:feature:auth`**: Login and Registration flows.
* **`:feature:posts`**: Social feed, creating posts, and commenting.
* **`:feature:profile`**: User profile, editing info, settings, and statistics.
* **`:feature:records`**: Workout history lists, record details, and record comparisons.
* **`:feature:tracking`**: Active GPS tracking, Route maps, and activity summaries (contains domain UseCases like `FinishActivityUseCase`).
* **`:feature:search`**: Searching for users/clubs.
* **`:feature:notification`**: Viewing system and social notifications.

---

## 3. Tech Stack & Key Libraries
* **Language:** Java
* **Dependency Injection:** Dagger Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`).
* **Local Database:** Room Database.
* **Networking:** Retrofit + OkHttp.
* **Image Loading:** Glide.
* **Mapping/GPS:** Mapbox.

---

## 4. STRICT CODING CONVENTIONS FOR AI

Read and adhere to these rules before writing any Android code:

* **RULE 1 - READ BEFORE WRITE (SYNC WITH EXISTING CODE):** Before generating new code, you MUST analyze existing files in the current module to match their style, naming conventions, and patterns.
* **RULE 2 - STRICT UI REUSE (CHECK `:core:system` FIRST):** For any task involving app themes, colors, dimensions, layouts, or UI components, you MUST first read the `:core:system` module (specifically `colors.xml`, `themes.xml`, `styles.xml`, and Java UI helpers). Do NOT create new colors, generic layouts, or custom views in feature modules if a reusable equivalent already exists in `:core:system`.
* **RULE 3 - VIEW RECYCLING (CRITICAL):** When writing `RecyclerView` or `ListView` Adapters, you MUST use the `ViewHolder` pattern. If you load images (using Glide), you MUST clear the previous image (`Glide.with(...).clear(imageView)`) and set a placeholder in the `else` branch to prevent images from mixing up during fast scrolling.
* **RULE 4 - LIVEDATA LIFECYCLE:** Never observe `LiveData` inside `onCreateDialog` or before the view is created. Always observe in `onViewCreated` using `getViewLifecycleOwner()` for Fragments.
* **RULE 5 - DEPENDENCY INJECTION:** Always use Hilt. Do not instantiate ViewModels or Repositories manually using `new`. 
* **RULE 6 - NO BLOCKING THE MAIN THREAD:** Network calls, Database queries, and heavy computations MUST be done on background threads.
* **RULE 7 - ENGLISH ONLY:** All code, variables, functions, and inline comments detailing business logic must be written in clear English.