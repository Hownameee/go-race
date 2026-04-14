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
(4, 'Night walk', 'Walking', '2026-03-25 06:03:15', '2024-06-01 06:32:41', 1766, 2.42, 132.4, 98, 4.93, 'records-image/1775377446793630de294-7102-4cc8-8b9d-f2d39e34a6d3.png'),
(4, 'Hangout', 'Running', '2026-03-24 17:05:20', '2024-06-02 17:48:11', 2571, 5.31, 382.1, 147, 7.43, 'records-image/17755306669089132c98f-4060-4d28-8982-5ef701ecae2f.png'),
(4, 'Afternoon', 'Running', '2026-03-23 08:01:10', '2024-06-04 09:05:45', 3875, 10.15, 715.3, 152, 9.43, 'records-image/1775376297001f94d3084-957c-4124-9089-02859e57f3a5.png'),
(4, 'Dosth', 'Walking', '2024-06-02 07:10:05', '2024-06-02 07:56:30', 2785, 3.85, 211.5, 102, 4.98, 'records-image/1775528097617e0f11976-36c2-406e-87a0-da463e8553fd.png'),
(4, 'Normal Walking', 'Running', '2024-06-03 19:15:00', '2024-06-03 20:08:22', 3202, 8.42, 605.8, 158, 9.47, 'records-image/1775549957819c32245e7-2a88-45af-8e50-671fe0829791.png'),
(4, 'Sunday the king play', 'Walking', '2024-06-05 08:08:40', '2024-06-05 09:12:15', 3815, 5.12, 286.7, 105, 4.83, 'records-image/17753774154385e055b96-7665-4a95-86a7-1f95fbe9914f.png'),
(5, 'Hardcore Walking', 'Running', '2024-06-06 06:02:15', '2024-06-06 06:51:30', 2955, 7.23, 512.4, 149, 8.81, 'records-image/17755308409948f73efc9-4394-4da2-b639-f5619ae732b0.png'),
(5, 'Monday Run', 'Walking', '2024-06-08 09:14:00', '2024-06-08 12:02:45', 10125, 11.45, 654.2, 112, 4.07, 'records-image/1775374318079685e8098-5634-4e3a-b054-fe78bd59f80b.png'),
(5, 'Walking', 'Running', '2024-06-09 17:05:00', '2024-06-09 18:37:12', 5532, 14.85, 1050.6, 161, 9.66, 'records-image/1775530365954692558c9-cfc2-490e-ab28-f5c3a8e7a35a.png'),
(5, 'With friend', 'Walking', '2024-06-10 18:02:30', '2024-06-10 18:41:15', 2325, 3.12, 172.4, 101, 4.83, 'records-image/17755303176251c992fed-3e65-4164-a437-74e59415219f.png'),
(5, 'Random Activity', 'Walking', '2024-06-11 07:15:00', '2024-06-11 07:31:12', 972, 1.25, 68.9, 94, 4.63, 'records-image/17755303412966e39c323-ab99-4100-961e-8992256aec4c.png'),
(5, 'Free time', 'Running', '2024-06-12 05:32:10', '2024-06-12 06:18:44', 2794, 6.88, 485.3, 154, 8.86, 'records-image/17753742718248a871091-c046-40b0-bb7d-aff9eeb4f3ca.png');

-- Insert mock posts
INSERT OR IGNORE INTO POST (record_id, owner_id, title, description, photo_url, like_count, comment_count, view_mode, created_at) VALUES
(1, 1, 'Morning Run', 'Great start to the day!', 'http://example.com/post1.jpg', 2, 3, 'Everyone', '2023-10-01 06:00:00'),
(2, 2, 'Evening Walk', 'Relaxing walk in the park.', 'http://example.com/post2.jpg', 1, 1, 'Followers', '2023-10-02 18:00:00'),
(3, 1, 'Interval Training', 'Pushed my limits today.', 'http://example.com/post3.jpg', 1, 1, 'Everyone', '2023-10-03 06:30:00'),
(NULL, 3, 'Rest Day', 'Taking a break today.', NULL, 1, 0, 'Everyone', '2023-10-04 07:00:00');

-- Insert mock comments (explict comment_id for parenting)
INSERT OR IGNORE INTO COMMENT (comment_id, post_id, user_id, content, like_count, reply_count, parent_id) VALUES
(1, 1, 2, 'Great job John!', 2, 1, NULL),
(2, 1, 3, 'Inspiring speed!', 0, 0, NULL),
(3, 2, 1, 'Looks so peaceful.', 1, 0, NULL),
(4, 3, 2, 'Keep it up!', 0, 0, NULL),
(5, 1, 3, 'I agree!', 0, 0, 1); -- Reply to comment 1

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