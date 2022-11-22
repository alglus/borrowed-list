TRUNCATE users RESTART IDENTITY CASCADE;

-- Users
INSERT INTO users (email, password, verified, role) VALUES ('name1@example.com', 'password1', true, 'USER');
INSERT INTO users (email, password, verified, role) VALUES ('name2@example.com', 'password2', true, 'USER');
INSERT INTO users (email, password, verified, role) VALUES ('name3@example.com', 'password3', true, 'ADMIN');
INSERT INTO users (email, password, verified, role) VALUES ('name4@example.com', 'password4', false, 'USER');