-- Insert mock users
INSERT OR IGNORE INTO USERS (username, email, hashed_password, birthdate, fullname, avatar_url) VALUES
('john_doe', 'john@example.com', 'hashed_pw_1', '2005-07-05', 'John Doe', 'http://example.com/john.jpg'),
('jane_smith', 'jane@example.com', 'hashed_pw_2', '2005-09-05', 'Jane Smith', 'http://example.com/jane.jpg'),
('runner_boy', 'runner@example.com', 'hashed_pw_3', '2005-08-05', 'Runner Boy', 'http://example.com/runner.jpg');
('KD', 'ld@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-08-05', 'Kim duyen', 'http://example.com/runner.jpg');

-- Insert mock records (Note: RECORD no longer has owner_id in your exact schema)
INSERT OR IGNORE INTO RECORD (activity_type, start_time, end_time, duration_seconds, distance_km, calories_burned, heart_rate_avg) VALUES
('Running', '2023-10-01 06:00:00', '2023-10-01 07:00:00', 3600, 10.5, 600, 145),
('Walking', '2023-10-02 18:00:00', '2023-10-02 19:00:00', 3600, 5.0, 250, 100),
('Running', '2023-10-03 06:30:00', '2023-10-03 07:15:00', 2700, 8.0, 450, 150),
('Walking', '2023-10-04 07:00:00', '2023-10-04 07:30:00', 1800, 2.5, 120, 95);

-- Insert mock posts
INSERT OR IGNORE INTO POST (record_id, owner_id, title, description, photo_url, like_count, comment_count, view_mode, created_at) VALUES
(1, 1, 'Morning Run', 'Great start to the day!', 'http://example.com/post1.jpg', 2, 2, 'Everyone', '2023-10-01 06:00:00'),
(2, 2, 'Evening Walk', 'Relaxing walk in the park.', 'http://example.com/post2.jpg', 1, 1, 'Followers', '2023-10-02 18:00:00'),
(3, 1, 'Interval Training', 'Pushed my limits today.', 'http://example.com/post3.jpg', 1, 1, 'Everyone', '2023-10-03 06:30:00'),
(NULL, 3, 'Rest Day', 'Taking a break today.', NULL, 1, 0, 'Everyone', '2023-10-04 07:00:00');

-- Insert mock comments
INSERT OR IGNORE INTO COMMENT (post_id, user_id, content) VALUES
(1, 2, 'Great job John!'),
(1, 3, 'Inspiring speed!'),
(2, 1, 'Looks so peaceful.'),
(3, 2, 'Keep it up!');

-- Insert mock likes
INSERT OR IGNORE INTO LIKE (post_id, user_id) VALUES
(1, 2),
(1, 3),
(2, 1),
(3, 2),
(4, 1);

-- Insert mock follows
INSERT OR IGNORE INTO FOLLOW (follower_id, following_id) VALUES
(2, 1), -- Jane follows John
(3, 1), -- Runner follows John
(1, 2), -- John follows Jane
(1, 3); -- John follows Runner Boy
