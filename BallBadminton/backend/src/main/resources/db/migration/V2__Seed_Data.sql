-- Seed Data for Development Environment
-- Telangana Ball Badminton Association Website
-- Version: 1.0.0

-- Insert sample users for development
INSERT INTO users (id, username, email, password, first_name, last_name, active, email_verified) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin', 'admin@telanganaballbadminton.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc5rGkLSMpXzdsQAeIWhy', 'System', 'Administrator', true, true),
('550e8400-e29b-41d4-a716-446655440002', 'editor', 'editor@telanganaballbadminton.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc5rGkLSMpXzdsQAeIWhy', 'Content', 'Editor', true, true),
('550e8400-e29b-41d4-a716-446655440003', 'moderator', 'moderator@telanganaballbadminton.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc5rGkLSMpXzdsQAeIWhy', 'Content', 'Moderator', true, true);

-- Assign roles to users
INSERT INTO user_roles (user_id, role) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'ADMIN'),
('550e8400-e29b-41d4-a716-446655440002', 'EDITOR'),
('550e8400-e29b-41d4-a716-446655440003', 'MODERATOR');

-- Insert sample association members
INSERT INTO members (id, name, position, email, phone, biography, hierarchy_level, tenure_start_date, is_active, is_prominent) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'Dr. Rajesh Kumar', 'President', 'president@telanganaballbadminton.org', '+91-9876543210', 'Dr. Rajesh Kumar has been actively involved in promoting Ball Badminton in Telangana for over 15 years. He holds a PhD in Sports Management and has represented India in international Ball Badminton championships.', 1, '2023-01-01', true, true),
('660e8400-e29b-41d4-a716-446655440002', 'Mrs. Priya Sharma', 'Secretary', 'secretary@telanganaballbadminton.org', '+91-9876543211', 'Mrs. Priya Sharma is a former national-level Ball Badminton player who has dedicated her post-playing career to administration and development of the sport in Telangana.', 2, '2023-01-01', true, true),
('660e8400-e29b-41d4-a716-446655440003', 'Mr. Venkat Reddy', 'Treasurer', 'treasurer@telanganaballbadminton.org', '+91-9876543212', 'Mr. Venkat Reddy brings over 20 years of financial management experience to the association. He is a Chartered Accountant and has been instrumental in securing funding for various tournaments.', 3, '2023-01-01', true, false),
('660e8400-e29b-41d4-a716-446655440004', 'Ms. Lakshmi Devi', 'Joint Secretary', 'jointsec@telanganaballbadminton.org', '+91-9876543213', 'Ms. Lakshmi Devi is responsible for coordinating district-level activities and has been pivotal in establishing Ball Badminton programs in rural areas of Telangana.', 4, '2023-01-01', true, false),
('660e8400-e29b-41d4-a716-446655440005', 'Mr. Suresh Babu', 'Technical Director', 'technical@telanganaballbadminton.org', '+91-9876543214', 'Mr. Suresh Babu is a certified Ball Badminton coach with international coaching credentials. He oversees technical aspects of tournaments and player development programs.', 5, '2023-01-01', true, false);

-- Insert sample prominent players
INSERT INTO players (id, name, date_of_birth, gender, district_id, category, contact_email, contact_phone, is_prominent, is_active) VALUES
('770e8400-e29b-41d4-a716-446655440001', 'Arjun Reddy', '1995-03-15', 'MALE', (SELECT id FROM districts WHERE name = 'Hyderabad'), 'MEN', 'arjun.reddy@email.com', '+91-9876543220', true, true),
('770e8400-e29b-41d4-a716-446655440002', 'Sneha Patel', '1998-07-22', 'FEMALE', (SELECT id FROM districts WHERE name = 'Warangal Urban'), 'WOMEN', 'sneha.patel@email.com', '+91-9876543221', true, true),
('770e8400-e29b-41d4-a716-446655440003', 'Kiran Kumar', '1992-11-08', 'MALE', (SELECT id FROM districts WHERE name = 'Karimnagar'), 'MEN', 'kiran.kumar@email.com', '+91-9876543222', true, true),
('770e8400-e29b-41d4-a716-446655440004', 'Divya Sharma', '1996-05-30', 'FEMALE', (SELECT id FROM districts WHERE name = 'Nizamabad'), 'WOMEN', 'divya.sharma@email.com', '+91-9876543223', true, true),
('770e8400-e29b-41d4-a716-446655440005', 'Ravi Teja', '2001-09-12', 'MALE', (SELECT id FROM districts WHERE name = 'Khammam'), 'JUNIOR', 'ravi.teja@email.com', '+91-9876543224', true, true);

-- Insert player statistics
INSERT INTO player_statistics (player_id, matches_played, matches_won, tournaments_participated, tournaments_won, win_percentage, current_ranking, best_ranking, total_points) VALUES
((SELECT id FROM players WHERE name = 'Arjun Reddy'), 45, 38, 12, 5, 84.44, 2, 1, 1250),
((SELECT id FROM players WHERE name = 'Sneha Patel'), 38, 32, 10, 4, 84.21, 1, 1, 1180),
((SELECT id FROM players WHERE name = 'Kiran Kumar'), 52, 41, 15, 3, 78.85, 3, 2, 1100),
((SELECT id FROM players WHERE name = 'Divya Sharma'), 35, 28, 9, 3, 80.00, 2, 1, 1050),
((SELECT id FROM players WHERE name = 'Ravi Teja'), 28, 22, 8, 2, 78.57, 1, 1, 950);

-- Insert sample achievements
INSERT INTO achievements (player_id, title, description, achievement_date, category, level, position, is_verified) VALUES
((SELECT id FROM players WHERE name = 'Arjun Reddy'), 'Telangana State Championship Winner', 'Won the men''s singles category in the annual state championship', '2023-12-15', 'Men Singles', 'STATE', 1, true),
((SELECT id FROM players WHERE name = 'Sneha Patel'), 'National Championship Runner-up', 'Secured second place in the women''s singles at national level', '2023-11-20', 'Women Singles', 'NATIONAL', 2, true),
((SELECT id FROM players WHERE name = 'Kiran Kumar'), 'South Zone Championship Winner', 'Champion in men''s doubles category at south zone level', '2023-10-10', 'Men Doubles', 'NATIONAL', 1, true),
((SELECT id FROM players WHERE name = 'Divya Sharma'), 'Telangana State Championship Winner', 'Won the women''s singles category in the state championship', '2023-12-15', 'Women Singles', 'STATE', 1, true),
((SELECT id FROM players WHERE name = 'Ravi Teja'), 'Junior National Championship Winner', 'Champion in junior men''s singles category', '2023-09-25', 'Junior Men Singles', 'NATIONAL', 1, true);

-- Insert sample tournaments
INSERT INTO tournaments (id, name, description, start_date, end_date, venue, district_id, registration_start_date, registration_end_date, max_participants, entry_fee, prize_money, status, tournament_type, age_category, gender_category, is_featured) VALUES
('880e8400-e29b-41d4-a716-446655440001', 'Telangana State Ball Badminton Championship 2024', 'Annual state-level championship featuring all categories of Ball Badminton competitions', '2024-03-15', '2024-03-18', 'Gachibowli Indoor Stadium', (SELECT id FROM districts WHERE name = 'Hyderabad'), '2024-02-01', '2024-03-01', 200, 500.00, 100000.00, 'REGISTRATION_OPEN', 'SINGLES', 'All', 'Mixed', true),
('880e8400-e29b-41d4-a716-446655440002', 'Warangal District Tournament', 'District-level tournament for promoting local talent', '2024-02-20', '2024-02-22', 'Warangal Sports Complex', (SELECT id FROM districts WHERE name = 'Warangal Urban'), '2024-01-15', '2024-02-10', 100, 300.00, 25000.00, 'REGISTRATION_OPEN', 'SINGLES', 'All', 'Mixed', false),
('880e8400-e29b-41d4-a716-446655440003', 'Junior Development Cup', 'Special tournament for junior players development', '2024-04-05', '2024-04-07', 'Karimnagar Sports Academy', (SELECT id FROM districts WHERE name = 'Karimnagar'), '2024-03-01', '2024-03-25', 80, 200.00, 15000.00, 'UPCOMING', 'SINGLES', 'Junior', 'Mixed', true);

-- Insert sample news articles
INSERT INTO news_articles (id, title, slug, summary, content, category_id, author, published_at, is_published, is_featured, language) VALUES
('990e8400-e29b-41d4-a716-446655440001', 'Telangana Ball Badminton Association Launches New Website', 'telangana-ball-badminton-association-launches-new-website', 'The association proudly announces the launch of its official website to better serve players and enthusiasts.', 'The Telangana Ball Badminton Association is excited to announce the launch of its brand new official website. This comprehensive platform will serve as the central hub for all information related to Ball Badminton in Telangana state. The website features player profiles, tournament information, news updates, and much more. We invite all players, coaches, and enthusiasts to explore the new platform and stay connected with the latest developments in Telangana Ball Badminton.', (SELECT id FROM news_categories WHERE slug = 'association'), 'Admin Team', '2024-01-15 10:00:00', true, true, 'ENGLISH'),
('990e8400-e29b-41d4-a716-446655440002', 'Registration Opens for State Championship 2024', 'registration-opens-state-championship-2024', 'Online registration is now open for the annual Telangana State Ball Badminton Championship 2024.', 'We are pleased to announce that registration for the Telangana State Ball Badminton Championship 2024 is now open. The championship will be held from March 15-18, 2024, at the Gachibowli Indoor Stadium in Hyderabad. This year''s tournament promises to be the biggest yet, with participants from all 33 districts of Telangana expected to compete. Registration deadline is March 1, 2024. Don''t miss this opportunity to showcase your skills at the state level!', (SELECT id FROM news_categories WHERE slug = 'tournaments'), 'Tournament Committee', '2024-02-01 09:00:00', true, true, 'ENGLISH'),
('990e8400-e29b-41d4-a716-446655440003', 'Sneha Patel Wins National Championship Silver', 'sneha-patel-wins-national-championship-silver', 'Telangana''s Sneha Patel secures second place in the women''s singles at the National Ball Badminton Championship.', 'Congratulations to Sneha Patel from Warangal for her outstanding performance at the National Ball Badminton Championship! Sneha secured the silver medal in the women''s singles category, bringing pride to Telangana state. Her journey to the finals was remarkable, defeating several top-ranked players from across the country. This achievement highlights the growing strength of Ball Badminton in Telangana and inspires young players to pursue excellence in the sport.', (SELECT id FROM news_categories WHERE slug = 'players'), 'Sports Reporter', '2023-11-22 14:30:00', true, false, 'ENGLISH');

-- Insert sample media galleries
INSERT INTO media_galleries (id, title, description, gallery_type, is_featured, is_public) VALUES
('aa0e8400-e29b-41d4-a716-446655440001', 'State Championship 2023 Highlights', 'Photo gallery from the Telangana State Ball Badminton Championship 2023', 'PHOTO', true, true),
('aa0e8400-e29b-41d4-a716-446655440002', 'Training Camp Videos', 'Video collection from various training camps organized by the association', 'VIDEO', false, true),
('aa0e8400-e29b-41d4-a716-446655440003', 'Association Events', 'Mixed media from various association events and meetings', 'MIXED', false, true);

-- Insert sample downloads/resources
INSERT INTO downloads (id, title, description, file_name, category, is_public, is_active) VALUES
('bb0e8400-e29b-41d4-a716-446655440001', 'Ball Badminton Rules and Regulations', 'Official rules and regulations for Ball Badminton as per international standards', 'ball_badminton_rules_2024.pdf', 'Rules', true, true),
('bb0e8400-e29b-41d4-a716-446655440002', 'Tournament Registration Form', 'Standard registration form for all tournaments organized by the association', 'tournament_registration_form.pdf', 'Forms', true, true),
('bb0e8400-e29b-41d4-a716-446655440003', 'Player Registration Guidelines', 'Guidelines for new player registration with the association', 'player_registration_guidelines.pdf', 'Guidelines', true, true),
('bb0e8400-e29b-41d4-a716-446655440004', 'Coaching Manual', 'Comprehensive coaching manual for Ball Badminton coaches', 'coaching_manual_2024.pdf', 'Training', true, true);

COMMIT;