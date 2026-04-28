-- Insert mock users (normal sign-up style: only required register fields)
INSERT OR IGNORE INTO USERS (
    username,
    fullname,
    email,
    hashed_password,
    birthdate
) VALUES
('john_doe', 'John Doe', 'john@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-07-05'),
('jane_smith', 'Jane Smith', 'jane@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-09-05'),
('runner_boy', 'Runner Boy', 'runner@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-08-05'),
('kd', 'Kim Duyen', 'kd@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-08-05'),
('nh', 'Nam Huynh', 'nh@example.com', '$2b$10$OKFQq2gUVgeOmsyEEuuAfuw9KL3Pgp4CYSjE0FRkWcSyeubg9nuHi', '2005-08-05');

-- Insert mock records
INSERT OR IGNORE INTO RECORD (
    owner_id,
    activity_type,
    title,
    start_time,
    end_time,
    duration_seconds,
    distance_km,
    calories_burned,
    heart_rate_avg,
    speed,
    s3_key
) VALUES
(1, 'Running', 'Sunrise Tempo', '2026-04-05 05:45:00', '2026-04-05 06:25:00', 2400, 6.40, 420.5, 152, 9.60, 'records-image/record-1.png'),
(1, 'Running', 'Park Intervals', '2026-04-07 18:10:00', '2026-04-07 18:46:00', 2160, 5.20, 365.3, 158, 8.67, 'records-image/record-2.png'),
(1, 'Walking', 'Recovery Walk', '2026-04-08 06:30:00', '2026-04-08 07:05:00', 2100, 2.75, 118.2, 101, 4.71, 'records-image/record-3.png'),
(2, 'Walking', 'Evening Walk', '2026-04-06 18:20:00', '2026-04-06 19:02:00', 2520, 3.40, 160.4, 103, 4.86, 'records-image/record-4.png'),
(2, 'Walking', 'Lake Loop', '2026-04-09 06:40:00', '2026-04-09 07:35:00', 3300, 4.25, 205.6, 106, 4.64, 'records-image/record-5.png'),
(3, 'Running', 'Track Repeats', '2026-04-04 17:30:00', '2026-04-04 18:18:00', 2880, 7.10, 498.1, 161, 8.88, 'records-image/record-6.png'),
(3, 'Running', 'Long Run', '2026-04-10 05:15:00', '2026-04-10 06:42:00', 5220, 12.60, 910.7, 156, 8.69, 'records-image/record-7.png'),
(4, 'Walking', 'Morning Steps', '2026-04-03 06:10:00', '2026-04-03 06:44:00', 2040, 2.90, 132.1, 100, 5.12, 'records-image/record-8.png'),
(4, 'Running', 'Riverside Run', '2026-04-11 17:50:00', '2026-04-11 18:36:00', 2760, 6.85, 472.8, 149, 8.93, 'records-image/record-9.png'),
(4, 'Walking', 'Weekend Walk', '2026-04-12 07:20:00', '2026-04-12 08:32:00', 4320, 5.95, 298.6, 104, 4.96, 'records-image/record-10.png'),
(5, 'Running', 'Campus Run', '2026-04-02 18:05:00', '2026-04-02 18:39:00', 2040, 4.95, 338.4, 150, 8.74, 'records-image/record-11.png'),
(5, 'Walking', 'Cooldown Walk', '2026-04-13 06:50:00', '2026-04-13 07:18:00', 1680, 2.10, 92.7, 97, 4.50, 'records-image/record-12.png');

-- Insert mock posts
INSERT OR IGNORE INTO POST (
    record_id,
    owner_id,
    title,
    description,
    photo_url,
    like_count,
    comment_count,
    view_mode,
    created_at
) VALUES
(1, 1, 'Morning Run', 'Great start to the day with a steady tempo.', 'https://example.com/posts/post-1.jpg', 2, 2, 'Everyone', '2026-04-05 06:30:00'),
(4, 2, 'Evening Walk', 'Relaxing walk in the park after class.', 'https://example.com/posts/post-2.jpg', 1, 1, 'Followers', '2026-04-06 19:10:00'),
(7, 3, 'Long Run Done', 'Felt strong through the final kilometers.', 'https://example.com/posts/post-3.jpg', 2, 1, 'Everyone', '2026-04-10 06:50:00'),
(NULL, 4, 'Rest Day', 'Taking it easy and stretching today.', NULL, 1, 0, 'Everyone', '2026-04-12 09:00:00');

-- Insert mock comments
INSERT OR IGNORE INTO COMMENT (
    comment_id,
    post_id,
    user_id,
    parent_id,
    content,
    like_count,
    reply_count,
    created_at
) VALUES
(1, 1, 2, NULL, 'Great job John!', 2, 1, '2026-04-05 07:00:00'),
(2, 1, 3, NULL, 'Nice pace out there.', 0, 0, '2026-04-05 07:10:00'),
(3, 2, 1, NULL, 'Looks so peaceful.', 1, 0, '2026-04-06 19:30:00'),
(4, 3, 2, NULL, 'Strong finish!', 0, 0, '2026-04-10 07:10:00'),
(5, 1, 3, 1, 'I agree, super consistent!', 0, 0, '2026-04-05 07:18:00');

-- Insert mock comment likes
INSERT OR IGNORE INTO COMMENT_LIKE (comment_id, user_id) VALUES
(1, 1),
(1, 3),
(3, 2);

-- Insert mock likes
INSERT OR IGNORE INTO LIKE (post_id, user_id) VALUES
(1, 2),
(1, 3),
(2, 1),
(3, 2),
(3, 5),
(4, 1);

-- Insert mock follows
INSERT OR IGNORE INTO FOLLOW (follower_id, following_id) VALUES
(2, 1),
(3, 1),
(1, 2),
(1, 3),
(4, 1),
(5, 3);

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

-- club mock data
INSERT INTO CLUBS (name, description, avatar_s3_key, privacy_type, leader_id)
VALUES 
('Hanoi Runners', 'Nhóm chạy vui vẻ khu vực Hồ Tây, giao lưu là chính. Mỗi tuần 1 lần mại zô mại zô', 'club-avatar-image/thobaymau.jpg', 'public', 1),
('Elite Marathoners', 'Nhóm chỉ dành cho các runners có pace dưới 5.0. Cần duyệt!', 'club-avatar-image/linux.png', 'private', 2),
('Weekend Joggers', 'Chạy nhẹ nhàng mỗi cuối tuần để rèn luyện sức khỏe.', 'club-avatar-image/Ai.jpeg', 'public', 4);

INSERT INTO CLUB_MEMBERS (club_id, user_id, role, status) VALUES 
(1, 1, 'admin', 'approved'),
(1, 3, 'member', 'approved'),
(1, 4, 'member', 'approved');

INSERT INTO CLUB_MEMBERS (club_id, user_id, role, status) VALUES 
(2, 2, 'admin', 'approved'),
(2, 1, 'admin', 'approved'),
(2, 5, 'member', 'pending');

INSERT INTO CLUB_MEMBERS (club_id, user_id, role, status) VALUES 
(3, 4, 'admin', 'approved'),
(3, 5, 'member', 'approved');


INSERT INTO CLUB_EVENTS (club_id, created_by, title, description, start_time, end_time) VALUES 
(1, 1, 'Hồ Tây Long Run', 'Chạy 1 vòng Hồ Tây 15km vào sáng Chủ Nhật tuần này nhé mọi người!', '2026-04-12 05:30:00', '2026-04-12 08:30:00'),
(2, 2, 'April Time Trial', 'Bài test pace 5km hàng tháng. Bắt buộc có mặt!', '2026-04-15 18:00:00', '2026-04-15 19:30:00');

INSERT INTO CLUB_EVENT_PARTICIPANTS (event_id, user_id) VALUES 
(1, 1), 
(1, 4);

INSERT INTO CLUB_EVENT_PARTICIPANTS (event_id, user_id) VALUES 
(2, 2);

INSERT INTO NOTIFICATIONS (user_id, type, actor_id, activity_id, title, message)
VALUES 
(2, 'club_join_request', 5, 2, 'Yêu cầu tham gia', 'Nam Huynh xin gia nhập Elite Marathoners'),
(3, 'club_event', 1, 1, 'Sự kiện mới', 'Hanoi Runners vừa tạo sự kiện: Hồ Tây Long Run'),
(4, 'club_announcement', 1, 1, 'Thông báo quan trọng', 'Hanoi Runners: Nhắc nhở đóng quỹ');
INSERT OR IGNORE INTO NOTIFICATIONS (
    user_id,
    type,
    actor_id,
    activity_id,
    title,
    message
) VALUES
(1, 'system', NULL, NULL, 'New Activity', 'Your running record has been saved.'),
(4, 'system', NULL, NULL, 'New Activity', 'Your walking record has been saved.'),
(3, 'follow', 5, NULL, 'New follower', 'Nam Huynh started following you.');
