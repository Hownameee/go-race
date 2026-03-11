-- just demo table will change in the future
CREATE TABLE IF NOT EXISTS RECORD (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    activity_type TEXT NOT NULL CHECK (
        activity_type IN ('Walking', 'Running')
    ),
    start_time DATETIME DEFAULT (DATETIME('now', 'localtime')),
    end_time DATETIME,
    duration_seconds INTEGER,
    distance_km REAL,
    calories_burned INTEGER,
    heart_rate_avg INTEGER
);

CREATE TABLE IF NOT EXISTS USER (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    display_name TEXT,
    bio TEXT,
    profile_picture_url TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
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
    FOREIGN KEY (owner_id) REFERENCES USER(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENT (
    comment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES POST(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LIKE (
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES POST(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FOLLOW (
    follower_id INTEGER NOT NULL,
    following_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES USER(user_id) ON DELETE CASCADE
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