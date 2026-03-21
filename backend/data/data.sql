INSERT INTO USERS (full_name, email, password)
VALUES ('Test User', 'test@gmail.com', '123456');

INSERT INTO NOTIFICATIONS (user_id, type, actor_id, activity_id, title, message)
VALUES (
    1,
    'system',
    NULL,
    NULL,
    'New Activity',
    'Your running record has been saved'
);