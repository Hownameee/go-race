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