-- Reset tables
TRUNCATE users RESTART IDENTITY CASCADE;
TRUNCATE items RESTART IDENTITY CASCADE;

-- Users
INSERT INTO users (email, password, verified, role) VALUES ('name1@example.com', 'password1', true, 'USER');
INSERT INTO users (email, password, verified, role) VALUES ('name2@example.com', 'password2', true, 'USER');


-- Items
INSERT INTO items (user_id, returned, type, category, title, whom, date, due_date, description)
VALUES (1, false, 'borrowed', 'OTHER', 'Item1', 'name3', '2022-11-11', '2022-11-12', 'description1');

INSERT INTO items (user_id, returned, type, category, title, whom, date, due_date, description)
VALUES (1, false, 'lent', 'CLOTHES', 'Item2', 'name2', '2022-11-11', '2022-11-12', 'description2');

INSERT INTO items (user_id, returned, type, category, title, whom, date, due_date, description)
VALUES (1, false, 'lent', 'CLOTHES', 'Item3', 'name2', '2022-11-11', '2022-11-12', 'description3');

INSERT INTO items (user_id, returned, type, category, title, whom, date, due_date, description)
VALUES (1, false, 'lent', 'CLOTHES', 'Item4', 'name1', '2022-11-11', '2022-11-12', 'description4');

INSERT INTO items (user_id, returned, type, category, title, whom, date, due_date, description)
VALUES (2, false, 'borrowed', 'MONEY', 'Item5', 'name4', '2022-11-11', '2022-11-12', 'description5');