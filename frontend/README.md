## Folder Structure

```text
frontend/
├── app/                    # The main application module bringing everything together.
│   └── src/                # Contains the entry points 
│
├── core/                   # Shared foundational modules used across the application and features.
│   ├── common/             # Common utility functions, helpers, and extensions.
│   ├── data/               # The data layer responsible for caching and storing data (Repositories, Database).
│   │   ├── dao/            # Data Access Objects for database interactions.
│   │   ├── model/          # Local database entities.
│   │   ├── repository/     # Repository implementations handling domain logic.
│   │   └── di/             # Dependency injection modules for the data layer.
│   ├── network/            # Network layer handling API communication via Retrofit & OkHttp.
│   │   ├── api/            # API Service interfaces.
│   │   ├── di/             # Network injection mapping.
│   │   ├── model/          # Network Data Transfer Objects (DTOs) and payloads.
│   │   ├── source/         # Network Data Sources wrapping API calls securely.
│   │   └── utils/          # Network utilities.
│   ├── model/              # Domain models representing entities in the system cleanly.
│   └── system/             # Themes and reuseable components.
│
├── feature/                # Feature modules containing UI, view models.
│   └── posts/              # A self-contained feature module handling everything related to "posts" (displaying feeds, viewing posts).
│       └── ui/             # Views (Fragment), ViewModels (PostViewModel), and Adapters (e.g., PostAdapter).
│
├── build.gradle.kts        # The root project build file, defining dependencies and plugins for all subprojects/modules.
├── settings.gradle.kts     # Project settings defining which modules are included in the build.
```
