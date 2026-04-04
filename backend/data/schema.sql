CREATE TABLE IF NOT EXISTS NOTIFICATIONS (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    type TEXT CHECK (type IN ('like','comment','follow','system')) NOT NULL,
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
