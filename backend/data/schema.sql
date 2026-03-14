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
  shirt_size TEXT CHECK (
    shirt_size IN (
      '2XS', 'XS', 'S', 'M',
      'L', 'XL', '2XL', '3XL', '4XL', '5XL'
    )
  ),

  -- timestamps
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME
);