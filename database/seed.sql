-- seed.sql
-- Fictional Demo Data for Student OS

-- Users (Password for all is 'password')
-- Note: In a real environment, you must use hashed passwords.
-- BCrypt hash for 'password' is roughly ... 
-- For demo purposes using direct SQL seed, you might need to register via the UI, 
-- or we will insert a pre-hashed string for 'password'
INSERT INTO users (id, email, password_hash, role) VALUES 
(1, 'admin@example.com', '$2a$10$hAZHFcGYO4y32HJ.bPPGIemUljIzF2XGeW7HDCEJBwb4Ygnuv4Vny', 'ADMIN'),
(2, 'student@example.com', '$2a$10$hAZHFcGYO4y32HJ.bPPGIemUljIzF2XGeW7HDCEJBwb4Ygnuv4Vny', 'STUDENT'),
(3, 'mong@example.com', '$2a$10$hAZHFcGYO4y32HJ.bPPGIemUljIzF2XGeW7HDCEJBwb4Ygnuv4Vny', 'STUDENT'),
(4, 'dara@example.com', '$2a$10$hAZHFcGYO4y32HJ.bPPGIemUljIzF2XGeW7HDCEJBwb4Ygnuv4Vny', 'STUDENT'),
(5, 'sokha@example.com', '$2a$10$hAZHFcGYO4y32HJ.bPPGIemUljIzF2XGeW7HDCEJBwb4Ygnuv4Vny', 'STUDENT');

-- Reset sequence for users
SELECT setval('users_id_seq', 5);

INSERT INTO profiles (user_id, first_name, last_name, bio, major) VALUES
(2, 'Demo', 'Student', 'Just a demo user', 'Computer Science'),
(3, 'Mong', 'Kul', 'I love coding and design.', 'Software Engineering'),
(4, 'Dara', 'S.', 'UI/UX enthusiast.', 'Graphic Design');

INSERT INTO tasks (user_id, title, description, status, priority) VALUES 
(3, 'Master Java', 'Finish the Java Web course', 'IN_PROGRESS', 'HIGH'),
(3, 'Read Database Book', 'Chapter 4-5', 'TODO', 'MEDIUM'),
(2, 'Submit Midterm', 'Student OS project', 'COMPLETED', 'URGENT');

INSERT INTO user_skills (user_id, skill_name, skill_level, type) VALUES 
(3, 'Java', 'ADVANCED', 'TEACH'),
(3, 'JSP', 'INTERMEDIATE', 'TEACH'),
(3, 'Video Editing', 'BEGINNER', 'LEARN'),
(4, 'UI/UX Design', 'EXPERT', 'TEACH'),
(4, 'Java', 'BEGINNER', 'LEARN');

INSERT INTO jobs (user_id, title, description, budget) VALUES 
(4, 'Need a Java Developer', 'Looking for someone to build a small Tomcat web app.', 150.00),
(2, 'Logo Design', 'I need a logo for my startup.', 50.00);

