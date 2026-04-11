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
    club_id INTEGER DEFAULT NULL,
    view_mode TEXT NOT NULL CHECK (view_mode IN ('Everyone', 'Followers', 'Self')) DEFAULT 'Everyone',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (record_id) REFERENCES RECORD(record_id) ON DELETE SET NULL,
    FOREIGN KEY (owner_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    FOREIGN KEY (club_id) REFERENCES CLUBS(club_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENT (
    comment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    post_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    parent_id INTEGER DEFAULT NULL,
    content TEXT NOT NULL,
    like_count INTEGER DEFAULT 0,
    reply_count INTEGER DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES POST(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES COMMENT(comment_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENT_LIKE (
    comment_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES COMMENT(comment_id) ON DELETE CASCADE,
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
CREATE INDEX IF NOT EXISTS idx_comment_post_created ON COMMENT(post_id, parent_id, created_at);

-- Index for efficient comment like lookups
CREATE INDEX IF NOT EXISTS idx_comment_like_user ON COMMENT_LIKE(comment_id, user_id);

-- Index for efficient like lookups
CREATE INDEX IF NOT EXISTS idx_like_post_user ON LIKE(post_id, user_id);

---FTS5 extension for search user
CREATE VIRTUAL TABLE IF NOT EXISTS USER_FTS USING FTS5(
    fullname,
    content="USERS",
    content_rowid="user_id"
);

---trigger FTS user
CREATE TRIGGER  IF NOT EXISTS USER_AI AFTER INSERT ON USERS BEGIN
  INSERT INTO USER_FTS(rowid, fullname)
  VALUES (NEW.user_id, NEW.fullname);
END;

CREATE TRIGGER IF NOT EXISTS USER_AD AFTER DELETE ON USERS BEGIN
  INSERT INTO USER_FTS(USER_FTS, rowid, fullname)
  VALUES('delete', OLD.user_id, OLD.fullname);
END;

CREATE TRIGGER IF NOT EXISTS USER_AU AFTER UPDATE ON USERS BEGIN
  INSERT INTO USER_FTS(USER_FTS, rowid, fullname)
  VALUES('delete', OLD.user_id, OLD.fullname);

  INSERT INTO USER_FTS(rowid, fullname)
  VALUES (NEW.user_id, NEW.fullname);
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
    member_count INTEGER DEFAULT 1,
    post_count INTEGER DEFAULT 0,
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

-- ==========================================
-- TRIGGERS CHO MEMBER_COUNT (Bảng CLUB_MEMBERS)
-- ==========================================

-- 1. Khi có người MỚI tham gia (và được duyệt luôn, ví dụ public club)
CREATE TRIGGER IF NOT EXISTS trigger_club_member_insert
AFTER INSERT ON CLUB_MEMBERS
WHEN NEW.status = 'approved'
BEGIN
    UPDATE CLUBS 
    SET member_count = member_count + 1 
    WHERE club_id = NEW.club_id;
END;

-- 2. Khi CẬP NHẬT trạng thái (ví dụ: Admin duyệt từ 'pending' -> 'approved' HOẶC kick từ 'approved' -> 'banned')
CREATE TRIGGER IF NOT EXISTS trigger_club_member_update
AFTER UPDATE OF status ON CLUB_MEMBERS
BEGIN
    -- Nếu chuyển thành 'approved' -> Tăng 1
    UPDATE CLUBS 
    SET member_count = member_count + 1 
    WHERE club_id = NEW.club_id 
      AND OLD.status != 'approved' 
      AND NEW.status = 'approved';
      
    -- Nếu đang từ 'approved' bị đổi sang cái khác (banned, rejected) -> Giảm 1
    UPDATE CLUBS 
    SET member_count = member_count - 1 
    WHERE club_id = NEW.club_id 
      AND OLD.status = 'approved' 
      AND NEW.status != 'approved';
END;

-- 3. Khi người dùng RỜI KHỎI club (xóa record)
CREATE TRIGGER IF NOT EXISTS trigger_club_member_delete
AFTER DELETE ON CLUB_MEMBERS
WHEN OLD.status = 'approved'
BEGIN
    UPDATE CLUBS 
    SET member_count = member_count - 1 
    WHERE club_id = OLD.club_id;
END;


-- ==========================================
-- TRIGGERS CHO POST_COUNT (Bảng POST)
-- ==========================================

-- 1. Khi có bài viết MỚI được đăng vào Club
CREATE TRIGGER IF NOT EXISTS trigger_club_post_insert
AFTER INSERT ON POST
WHEN NEW.club_id IS NOT NULL
BEGIN
    UPDATE CLUBS 
    SET post_count = post_count + 1 
    WHERE club_id = NEW.club_id;
END;

-- 3. Khi bài viết trong Club bị XÓA
CREATE TRIGGER IF NOT EXISTS trigger_club_post_delete
AFTER DELETE ON POST
WHEN OLD.club_id IS NOT NULL
BEGIN
    UPDATE CLUBS 
    SET post_count = post_count - 1 
    WHERE club_id = OLD.club_id;
END;