# ⚙️ BACKEND_SKILL.md - Node.js Server Manual

This document provides the architectural rules, directory structure, and coding conventions for the backend API of the application. The backend is built with Node.js, Express, and SQLite.

---

## 1. Architecture Pattern (Strict Layering)
The backend strictly follows a Layered Architecture to separate concerns. Data must flow sequentially through these layers. **Do not skip layers.**

**Flow:** `Route` ➡️ `Controller` ➡️ `Service` ➡️ `Repository` / `Utils`

* **Routes (`routes/`):** Defines API endpoints, attaches validation middlewares, and routes requests to the appropriate Controller.
* **Controllers (`controllers/`):** Handles HTTP request/response logic ONLY. It extracts data from `req`, passes it to the Service, and formats the response using `res.ok()`, `res.created()`, or `res.error()` (via `restResponse.js`). **No business logic here.**
* **Services (`services/`):** The heart of the application. Contains all business logic, validation logic, and orchestration. It calls Repositories to interact with the database or Utils for external tools.
* **Repositories (`repo/`):** Pure database interaction. Contains raw SQL queries. **No business logic here.** Returns data to the Service.
* **Utils (`utils/`):** External integrations (S3, Firebase, Mapbox API), validation schemas (Zod), and shared helper functions.

---

## 2. Directory Guide

* **`config/`**: Centralized environment variable getters and configuration files (e.g., Mail, Map, S3, Firebase).
* **`data/`**: Database initialization and state. Contains `schema.sql` (table definitions), `data.sql` (mock/sample data), and `seed.js` (script to reset and populate the DB).
* **`middlewares/`**: Express middlewares for authorization (`auth.middleware.js`), input validation (`validation.js`), global error handling (`errorHandler.js`), and custom response formatting (`restResponse.js`).
* **`utils/schemas/`**: Zod schemas used for validating incoming request bodies/params.

---

## 3. Database Rules (SQLite)
* We use raw SQL queries via standard database drivers. **Do not use ORMs.**
* Always use parameterized queries (`?`) to prevent SQL injection.
* Foreign keys must utilize `ON DELETE CASCADE` or `ON DELETE SET NULL` to maintain data integrity.
* FTS5 extensions are used for high-performance searches (e.g., searching for Users).
* Reference **Section 5** for the exact current database schema.

---

## 4. STRICT CODING CONVENTIONS FOR AI

Read and adhere to these rules before writing any backend code:

* **RULE 1 - READ BEFORE WRITE (SYNC WITH EXISTING CODE):** Before generating new code, you MUST analyze existing files in the same layer to match their style, naming conventions, and error-handling patterns. Do not invent new patterns if a standard one already exists in the project.
* **RULE 2 - SCHEMA-DRIVEN VALIDATION:** All incoming POST/PUT request data must be validated using a Zod schema defined in `utils/schemas/` before the Controller processes it.
* **RULE 3 - ERROR HANDLING:** Never crash the server. Catch errors in the Service layer and throw appropriate custom errors or pass them to the Controller so `restResponse.js` can return a standardized JSON error response.
* **RULE 4 - NO HARDCODED SECRETS:** Always retrieve API keys, tokens, and secrets from the `config/` folder.
* **RULE 5 - ENGLISH ONLY:** All code, variables, functions, and inline comments detailing business logic must be written in clear English.

---

## 5. Current Database Schema

When writing SQL queries in the `repo/` layer, strictly adhere to the following table structures, foreign keys, and indexes:

```sql
CREATE TABLE IF NOT EXISTS NOTIFICATIONS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    type TEXT CHECK (type IN ('like','comment','follow','system', 'club_join_request', 'club_approved', 'club_event', 'club_announcement')) NOT NULL,
    actor_id INTEGER,
    activity_id INTEGER,
    title TEXT NOT NULL,
    message TEXT,
    read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS DEVICE_TOKENS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    token TEXT NOT NULL UNIQUE,
    platform TEXT DEFAULT 'android',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS USERS (
  user_id INTEGER PRIMARY KEY AUTOINCREMENT,
  role TEXT DEFAULT 'user' CHECK (
    role IN ('user', 'admin')
  ),

  -- sign-up information
  username TEXT NOT NULL,
  fullname TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  hashed_password TEXT NOT NULL,
  birthdate DATETIME NOT NULL,

  -- extra information (profile)
  avatar_url TEXT,
  nationality TEXT,
  address TEXT, -- "street, ward, province / city, country"
  height_cm REAL,
  weight_kg REAL,

  -- timestamps
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS RECORD (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id INTEGER NOT NULL,
    activity_type TEXT NOT NULL CHECK (
        activity_type IN ('Walking', 'Running')
    ),
    title TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    duration_seconds INTEGER,
    distance_km REAL,
    calories_burned REAL,
    heart_rate_avg REAL,
    speed REAL,
    s3_key TEXT,

    FOREIGN KEY (owner_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS POST (
    post_id INTEGER PRIMARY KEY AUTOINCREMENT,
    record_id INTEGER,
    owner_id INTEGER NOT NULL,
    title TEXT,
    description TEXT,
    photo_url TEXT,
    like_count INTEGER DEFAULT 0,
    comment_count INTEGER DEFAULT 0,
    view_mode TEXT NOT NULL CHECK (view_mode IN ('Everyone', 'Followers', 'Self')) DEFAULT 'Everyone',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (record_id) REFERENCES RECORD(record_id) ON DELETE SET NULL,
    FOREIGN KEY (owner_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENT (
    comment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES POST(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LIKE (
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES POST(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FOLLOW (
    follower_id INTEGER NOT NULL,
    following_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- Indexes for efficient cursor-based pagination on FOLLOW
CREATE INDEX IF NOT EXISTS idx_follow_following ON FOLLOW(following_id, created_at);
CREATE INDEX IF NOT EXISTS idx_follow_follower ON FOLLOW(follower_id, created_at);

-- Index for efficient cursor-based pagination on POST
CREATE INDEX IF NOT EXISTS idx_post_created_at ON POST(created_at);

-- Indexes for efficient cursor-based pagination on COMMENT
CREATE INDEX IF NOT EXISTS idx_comment_post_created ON COMMENT(post_id, created_at);

-- Index for efficient like lookups
CREATE INDEX IF NOT EXISTS idx_like_post_user ON LIKE(post_id, user_id);

---FTS5 extension for search user
CREATE VIRTUAL TABLE IF NOT EXISTS USER_FTS USING FTS5(
    username,
    content="USERS",
    content_rowid="user_id"
);

---trigger FTS user
CREATE TRIGGER  IF NOT EXISTS USER_AI AFTER INSERT ON USERS BEGIN
  INSERT INTO USER_FTS(rowid, username)
  VALUES (NEW.user_id, NEW.username);
END;

CREATE TRIGGER IF NOT EXISTS USER_AD AFTER DELETE ON USERS BEGIN
  INSERT INTO USER_FTS(USER_FTS, rowid, username)
  VALUES('delete', OLD.user_id, OLD.username);
END;

CREATE TRIGGER IF NOT EXISTS USER_AU AFTER UPDATE ON USERS BEGIN
  INSERT INTO USER_FTS(USER_FTS, rowid, username)
  VALUES('delete', OLD.user_id, OLD.username);

  INSERT INTO USER_FTS(rowid, username)
  VALUES (NEW.user_id, NEW.username);
END;

CREATE TABLE IF NOT EXISTS ROUTE_POINTS (
    point_id INTEGER PRIMARY KEY AUTOINCREMENT,
    record_id INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    altitude REAL,
    timestamp DATETIME NOT NULL,
    accuracy REAL,
    FOREIGN KEY (record_id) REFERENCES RECORD(record_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_route_points_record ON ROUTE_POINTS(record_id);

-- club
-- trang riêng club
CREATE TABLE IF NOT EXISTS CLUBS (
    club_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    avatar_s3_key TEXT,
    privacy_type TEXT DEFAULT 'public' CHECK (privacy_type IN ('public', 'private')), -- public: vào thẳng, private: cần duyệt (Req 5)
    leader_id INTEGER NOT NULL, -- Club Leader
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (leader_id) REFERENCES USERS(user_id) ON DELETE SET NULL
);

-- quản lý duyệt xóa theo role
CREATE TABLE IF NOT EXISTS CLUB_MEMBERS (
    club_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    role TEXT DEFAULT 'member' CHECK (role IN ('admin', 'member')),
    status TEXT DEFAULT 'approved' CHECK (status IN ('pending', 'approved', 'rejected')), -- pending dành cho private club
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (club_id, user_id),
    FOREIGN KEY (club_id) REFERENCES CLUBS(club_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- tạo sự kiện club
CREATE TABLE IF NOT EXISTS CLUB_EVENTS (
    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    club_id INTEGER NOT NULL,
    created_by INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (club_id) REFERENCES CLUBS(club_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES USERS(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS CLUB_EVENT_PARTICIPANTS (
    event_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES CLUB_EVENTS(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);