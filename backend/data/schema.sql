CREATE TABLE IF NOT EXISTS Record (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id INTEGER NOT NULL,
    activity_type TEXT NOT NULL CHECK (
        activity_type IN ('Walking', 'Running')
    ),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    duration_seconds INTEGER,
    distance_km REAL,
    calories_burned REAL,
    heart_rate_avg INTEGER,
    speed REAL,
    route_url TEXT
);