-- Migration V13: Additional Seed Data
-- Adds sample privacy consent records and additional test data
-- Author: Telangana Ball Badminton Association
-- Version: 1.0.0

-- Insert sample privacy consent records for existing users
INSERT INTO privacy_consents (user_id, consent_type, consent_given, consent_date, privacy_policy_version, ip_address) VALUES
((SELECT id FROM users WHERE username = 'admin'), 'PRIVACY_POLICY', true, CURRENT_TIMESTAMP, '1.0', '127.0.0.1'),
((SELECT id FROM users WHERE username = 'admin'), 'DATA_PROCESSING', true, CURRENT_TIMESTAMP, '1.0', '127.0.0.1'),
((SELECT id FROM users WHERE username = 'editor'), 'PRIVACY_POLICY', true, CURRENT_TIMESTAMP, '1.0', '127.0.0.1'),
((SELECT id FROM users WHERE username = 'editor'), 'DATA_PROCESSING', true, CURRENT_TIMESTAMP, '1.0', '127.0.0.1'),
((SELECT id FROM users WHERE username = 'moderator'), 'PRIVACY_POLICY', true, CURRENT_TIMESTAMP, '1.0', '127.0.0.1'),
((SELECT id FROM users WHERE username = 'moderator'), 'DATA_PROCESSING', true, CURRENT_TIMESTAMP, '1.0', '127.0.0.1');

-- Insert sample media items for existing galleries
INSERT INTO media_items (gallery_id, title, file_url, thumbnail_url, media_type, sort_order, is_active) VALUES
((SELECT id FROM media_galleries WHERE title = 'State Championship 2023 Highlights'), 'Championship Opening Ceremony', '/media/images/championship_opening.jpg', '/media/thumbnails/championship_opening_thumb.jpg', 'IMAGE', 1, true),
((SELECT id FROM media_galleries WHERE title = 'State Championship 2023 Highlights'), 'Finals Match Action', '/media/images/finals_action.jpg', '/media/thumbnails/finals_action_thumb.jpg', 'IMAGE', 2, true),
((SELECT id FROM media_galleries WHERE title = 'State Championship 2023 Highlights'), 'Award Ceremony', '/media/images/award_ceremony.jpg', '/media/thumbnails/award_ceremony_thumb.jpg', 'IMAGE', 3, true),
((SELECT id FROM media_galleries WHERE title = 'Training Camp Videos'), 'Basic Techniques Training', '/media/videos/basic_techniques.mp4', '/media/thumbnails/basic_techniques_thumb.jpg', 'VIDEO', 1, true),
((SELECT id FROM media_galleries WHERE title = 'Training Camp Videos'), 'Advanced Strategies Session', '/media/videos/advanced_strategies.mp4', '/media/thumbnails/advanced_strategies_thumb.jpg', 'VIDEO', 2, true);

-- Insert sample tournament registrations
INSERT INTO tournament_registrations (tournament_id, player_id, registration_date, payment_status, payment_amount, status) VALUES
((SELECT id FROM tournaments WHERE name = 'Telangana State Ball Badminton Championship 2024'), (SELECT id FROM players WHERE name = 'Arjun Reddy'), '2024-02-05 10:30:00', 'PAID', 500.00, 'CONFIRMED'),
((SELECT id FROM tournaments WHERE name = 'Telangana State Ball Badminton Championship 2024'), (SELECT id FROM players WHERE name = 'Sneha Patel'), '2024-02-06 14:15:00', 'PAID', 500.00, 'CONFIRMED'),
((SELECT id FROM tournaments WHERE name = 'Telangana State Ball Badminton Championship 2024'), (SELECT id FROM players WHERE name = 'Kiran Kumar'), '2024-02-07 09:45:00', 'PAID', 500.00, 'CONFIRMED'),
((SELECT id FROM tournaments WHERE name = 'Warangal District Tournament'), (SELECT id FROM players WHERE name = 'Sneha Patel'), '2024-01-20 16:20:00', 'PAID', 300.00, 'CONFIRMED'),
((SELECT id FROM tournaments WHERE name = 'Junior Development Cup'), (SELECT id FROM players WHERE name = 'Ravi Teja'), '2024-03-05 11:00:00', 'PENDING', 200.00, 'REGISTERED');

-- Insert additional sample members for better hierarchy representation
INSERT INTO members (id, name, position, email, phone, biography, hierarchy_level, tenure_start_date, is_active, is_prominent) VALUES
('660e8400-e29b-41d4-a716-446655440006', 'Mr. Ramesh Naidu', 'Vice President', 'vicepresident@telanganaballbadminton.org', '+91-9876543215', 'Mr. Ramesh Naidu has been instrumental in developing Ball Badminton infrastructure across Telangana districts.', 2, '2023-01-01', true, true),
('660e8400-e29b-41d4-a716-446655440007', 'Dr. Sunitha Rao', 'Medical Officer', 'medical@telanganaballbadminton.org', '+91-9876543216', 'Dr. Sunitha Rao ensures player health and safety during tournaments and training sessions.', 6, '2023-01-01', true, false),
('660e8400-e29b-41d4-a716-446655440008', 'Mr. Krishna Murthy', 'Equipment Manager', 'equipment@telanganaballbadminton.org', '+91-9876543217', 'Mr. Krishna Murthy manages all equipment and facility requirements for association activities.', 7, '2023-01-01', true, false);

-- Insert additional sample players from different districts
INSERT INTO players (id, name, date_of_birth, gender, district_id, category, contact_email, contact_phone, is_prominent, is_active) VALUES
('770e8400-e29b-41d4-a716-446655440006', 'Pradeep Kumar', '1990-08-14', 'MALE', (SELECT id FROM districts WHERE name = 'Medak'), 'MEN', 'pradeep.kumar@email.com', '+91-9876543225', false, true),
('770e8400-e29b-41d4-a716-446655440007', 'Anitha Reddy', '1994-12-03', 'FEMALE', (SELECT id FROM districts WHERE name = 'Nalgonda'), 'WOMEN', 'anitha.reddy@email.com', '+91-9876543226', false, true),
('770e8400-e29b-41d4-a716-446655440008', 'Suresh Babu', '1988-04-25', 'MALE', (SELECT id FROM districts WHERE name = 'Rangareddy'), 'SENIOR', 'suresh.babu@email.com', '+91-9876543227', false, true),
('770e8400-e29b-41d4-a716-446655440009', 'Kavitha Sharma', '2002-06-18', 'FEMALE', (SELECT id FROM districts WHERE name = 'Siddipet'), 'JUNIOR', 'kavitha.sharma@email.com', '+91-9876543228', false, true),
('770e8400-e29b-41d4-a716-446655440010', 'Rajesh Goud', '1993-10-07', 'MALE', (SELECT id FROM districts WHERE name = 'Sangareddy'), 'MEN', 'rajesh.goud@email.com', '+91-9876543229', false, true);

-- Insert player statistics for new players
INSERT INTO player_statistics (player_id, matches_played, matches_won, tournaments_participated, tournaments_won, win_percentage, current_ranking, total_points) VALUES
((SELECT id FROM players WHERE name = 'Pradeep Kumar'), 25, 18, 6, 1, 72.00, 15, 650),
((SELECT id FROM players WHERE name = 'Anitha Reddy'), 30, 22, 8, 2, 73.33, 12, 720),
((SELECT id FROM players WHERE name = 'Suresh Babu'), 40, 28, 10, 1, 70.00, 8, 850),
((SELECT id FROM players WHERE name = 'Kavitha Sharma'), 18, 14, 5, 1, 77.78, 3, 480),
((SELECT id FROM players WHERE name = 'Rajesh Goud'), 22, 16, 7, 0, 72.73, 18, 580);

-- Insert additional news articles
INSERT INTO news_articles (id, title, slug, summary, content, category_id, author, published_at, is_published, is_featured, language) VALUES
('990e8400-e29b-41d4-a716-446655440004', 'New Training Facilities Inaugurated in Warangal', 'new-training-facilities-inaugurated-warangal', 'State-of-the-art Ball Badminton training facilities have been inaugurated in Warangal district.', 'The Telangana Ball Badminton Association is proud to announce the inauguration of new training facilities in Warangal district. These modern facilities include indoor courts with proper lighting, equipment storage, and player amenities. The facilities will serve as a regional training center for players from surrounding districts and will host regular coaching camps and development programs.', (SELECT id FROM news_categories WHERE slug = 'association'), 'Infrastructure Team', '2024-01-20 15:30:00', true, false, 'ENGLISH'),
('990e8400-e29b-41d4-a716-446655440005', 'Coaching Certification Program Announced', 'coaching-certification-program-announced', 'New coaching certification program launched to develop qualified Ball Badminton coaches across Telangana.', 'The association has launched a comprehensive coaching certification program aimed at developing qualified Ball Badminton coaches throughout Telangana state. The program covers technical skills, sports psychology, injury prevention, and tournament management. Successful candidates will receive official certification and will be eligible to conduct coaching sessions at district and state levels.', (SELECT id FROM news_categories WHERE slug = 'events'), 'Technical Committee', '2024-01-25 11:00:00', true, false, 'ENGLISH');

-- Insert sample audit log entries
INSERT INTO audit_logs (user_id, username, action, entity_type, entity_id, description, ip_address, request_method, request_url, status_code, severity, status) VALUES
((SELECT id FROM users WHERE username = 'admin'), 'admin', 'LOGIN', 'User', (SELECT id FROM users WHERE username = 'admin')::text, 'User logged in successfully', '127.0.0.1', 'POST', '/api/v1/auth/login', 200, 'INFO', 'SUCCESS'),
((SELECT id FROM users WHERE username = 'admin'), 'admin', 'CREATE', 'Member', '660e8400-e29b-41d4-a716-446655440006', 'Created new member: Mr. Ramesh Naidu', '127.0.0.1', 'POST', '/api/v1/members', 201, 'INFO', 'SUCCESS'),
((SELECT id FROM users WHERE username = 'editor'), 'editor', 'CREATE', 'NewsArticle', '990e8400-e29b-41d4-a716-446655440004', 'Created news article: New Training Facilities Inaugurated in Warangal', '127.0.0.1', 'POST', '/api/v1/news', 201, 'INFO', 'SUCCESS'),
((SELECT id FROM users WHERE username = 'moderator'), 'moderator', 'UPDATE', 'NewsArticle', '990e8400-e29b-41d4-a716-446655440005', 'Approved news article for publication', '127.0.0.1', 'PUT', '/api/v1/news/990e8400-e29b-41d4-a716-446655440005/approve', 200, 'INFO', 'SUCCESS');

COMMIT;