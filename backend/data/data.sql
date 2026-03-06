INSERT INTO USER (full_name, email, password)
VALUES ('Test User', 'test@gmail.com', '123456');

INSERT INTO NOTIFICATION (user_id, title, message, type)
VALUES (
    1,
    'New Activity',
    'Your running record has been saved',
    'Activity'
);