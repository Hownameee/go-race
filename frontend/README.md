## Folder Structure

```text
frontend/
├── app/                    # The main application module bringing everything together.
│   └── src/                # Contains the entry points 
│
├── core/                   # Shared foundational modules used across the application and features.
│   ├── common/             # Common utility functions, helpers, and extensions.
│   ├── data/               # The data layer responsible for fetching, caching, and storing data (Repositories,     Network, Database).
│   ├── model/              # Domain models and data structures used throughout the app.
│   └── system/             # Themes and reuseable components.
│
├── feature/                # Feature modules containing UI, view models.
│   └── posts/              # A self-contained feature module handling everything related to "posts" (displaying feeds, viewing posts).
│
├── build.gradle.kts        # The root project build file, defining dependencies and plugins for all subprojects/modules.
├── settings.gradle.kts     # Project settings defining which modules are included in the build.
```
