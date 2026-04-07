-- Insert mock users
INSERT OR IGNORE INTO USERS (username, email, hashed_password, birthdate, fullname, avatar_url) VALUES
('john_doe', 'john@example.com', 'hashed_pw_1', '2005-07-05', 'John Doe', 'http://example.com/john.jpg'),
('jane_smith', 'jane@example.com', 'hashed_pw_2', '2005-09-05', 'Jane Smith', 'http://example.com/jane.jpg'),
('runner_boy', 'runner@example.com', 'hashed_pw_3', '2005-08-05', 'Runner Boy', 'http://example.com/runner.jpg'),
('KD', 'ld@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-08-05', 'Kim duyen', 'http://example.com/runner.jpg'),
('NH', 'nh@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-08-05', 'Nam Huynh', 'http://example.com/runner.jpg');

-- Insert mock records (Note: RECORD no longer has owner_id in your exact schema)
INSERT INTO Record (owner_id, title, activity_type, start_time, end_time, duration_seconds, distance_km, calories_burned, heart_rate_avg, speed, s3_key)
VALUES 
(4, 'Walking', 'Walking', '2026-03-25 06:03:15', '2024-06-01 06:32:41', 1766, 2.42, 132.4, 98, 4.93, 'records-image/record.png'),
(4, 'Walking', 'Running', '2026-03-24 17:05:20', '2024-06-02 17:48:11', 2571, 5.31, 382.1, 147, 7.43, 'records-image/record.png'),
(4, 'Walking', 'Running', '2026-03-23 08:01:10', '2024-06-04 09:05:45', 3875, 10.15, 715.3, 152, 9.43, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-02 07:10:05', '2024-06-02 07:56:30', 2785, 3.85, 211.5, 102, 4.98, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-03 19:15:00', '2024-06-03 20:08:22', 3202, 8.42, 605.8, 158, 9.47, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-05 08:08:40', '2024-06-05 09:12:15', 3815, 5.12, 286.7, 105, 4.83, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-06 06:02:15', '2024-06-06 06:51:30', 2955, 7.23, 512.4, 149, 8.81, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-08 09:14:00', '2024-06-08 12:02:45', 10125, 11.45, 654.2, 112, 4.07, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-09 17:05:00', '2024-06-09 18:37:12', 5532, 14.85, 1050.6, 161, 9.66, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-10 18:02:30', '2024-06-10 18:41:15', 2325, 3.12, 172.4, 101, 4.83, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-11 07:15:00', '2024-06-11 07:31:12', 972, 1.25, 68.9, 94, 4.63, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-12 05:32:10', '2024-06-12 06:18:44', 2794, 6.88, 485.3, 154, 8.86, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-13 19:05:22', '2024-06-13 19:37:41', 1939, 2.54, 145.2, 99, 4.72, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-14 06:33:15', '2024-06-14 07:05:50', 1955, 2.71, 153.8, 104, 4.99, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-15 18:04:05', '2024-06-15 18:27:18', 1393, 3.45, 245.1, 159, 8.92, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-16 06:05:30', '2024-06-16 08:12:45', 7635, 21.35, 1520.4, 165, 10.07, 'records-image/record.png'),
(4, 'Walking', 'Running', '2024-06-17 17:34:12', '2024-06-17 18:19:55', 2743, 6.42, 452.9, 148, 8.43, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-18 08:02:10', '2024-06-18 09:28:45', 5195, 6.84, 395.6, 107, 4.74, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-19 12:05:20', '2024-06-19 12:48:15', 2575, 3.32, 184.2, 103, 4.64, 'records-image/record.png'),
(4, 'Walking', 'Walking', '2024-06-20 07:10:00', '2024-06-20 10:45:30', 12930, 15.62, 905.5, 114, 4.35, 'records-image/record.png');

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

-- Insert mock notifications
INSERT INTO NOTIFICATIONS (user_id, type, actor_id, activity_id, title, message)
VALUES (
    1,
    'system',
    NULL,
    NULL,
    'New Activity',
    'Your running record has been saved'
),
(
    4,
    'system',
    NULL,
    NULL,
    'New Activity',
    'Your running record has been saved'
);
