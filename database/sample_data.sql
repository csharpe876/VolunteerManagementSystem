-- Insert sample data for testing

-- Insert System Administrators
INSERT INTO SystemAdmin (username, password_hash, email, first_name, last_name, role, account_status) VALUES
('admin', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', 'admin@fstgc.uwi.edu', 'System', 'Administrator', 'super_admin', 'active'),
('coordinator', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', 'coordinator@fstgc.uwi.edu', 'Event', 'Coordinator', 'coordinator', 'active');
-- Password for both: admin123

-- Insert Sample Volunteers
INSERT INTO Volunteer (first_name, last_name, email, phone, password_hash, date_of_birth, address, skills, is_active, total_hours) VALUES
('Carl', 'Sharpe', 'carl.sharpe@mymona.uwi.edu', '876-555-0101', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', '2003-05-15', 'Kingston, Jamaica', 'Programming, Event Planning', TRUE, 25.5),
('Jaedon', 'Beckford', 'jaedon.beckford@mymona.uwi.edu', '876-555-0102', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', '2002-08-20', 'Kingston, Jamaica', 'Graphics Design, Social Media', TRUE, 18.0),
('Ti-Carla', 'Thompson', 'ti-carla.thompson@mymona.uwi.edu', '876-555-0103', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', '2003-02-10', 'Kingston, Jamaica', 'Communication, Tutoring', TRUE, 32.0),
('Ashani', 'Falconer', 'ashani.falconer@mymona.uwi.edu', '876-555-0104', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', '2003-11-30', 'Kingston, Jamaica', 'Leadership, Organization', TRUE, 40.5),
('Oneil', 'Marshall', 'oneil.marshall@mymona.uwi.edu', '876-555-0105', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', '2002-07-22', 'Kingston, Jamaica', 'Technical Support, Documentation', TRUE, 15.0);
-- Password for all volunteers: volunteer123

-- Insert Sample Events
INSERT INTO Event (event_name, description, event_date, location, capacity, status, created_by) VALUES
('Beach Cleanup', 'Community beach cleanup at Hellshire Beach', '2025-12-15 08:00:00', 'Hellshire Beach', 50, 'published', 1),
('Mentorship Program', 'Mentor first year students in Computer Science', '2025-12-20 14:00:00', 'FST Building, Room 201', 20, 'published', 1),
('Science Fair', 'Assist with high school science fair organization', '2026-01-10 09:00:00', 'UWI Mona Campus', 30, 'published', 1),
('Coding Workshop', 'Teach basic programming to high school students', '2026-01-15 10:00:00', 'Computer Lab 3', 15, 'draft', 2);

-- Insert Sample Announcements
INSERT INTO Announcement (title, content, priority, target_audience, status, posted_by) VALUES
('Welcome to VMS', 'Welcome to the Volunteer Management System! We are excited to have you join our community of dedicated volunteers.', 'high', 'all', 'published', 1),
('Upcoming Beach Cleanup', 'Don''t forget to register for our beach cleanup event on December 15th! Let''s make a difference together.', 'medium', 'all', 'published', 1),
('New Mentorship Opportunities', 'We are looking for experienced students to mentor first-years. Sign up now!', 'medium', 'volunteers', 'published', 2);

-- Insert Sample Awards
INSERT INTO AwardCriteria (award_name, description, criteria_type, threshold_value, badge_icon) VALUES
('Getting Started', 'Complete your first 5 hours of volunteer work', 'hours', 5, '🌟'),
('Committed Volunteer', 'Complete 25 hours of volunteer work', 'hours', 25, '⭐'),
('Dedicated Volunteer', 'Complete 50 hours of volunteer work', 'hours', 50, '💫'),
('Super Volunteer', 'Complete 100 hours of volunteer work', 'hours', 100, '🏆'),
('Event Enthusiast', 'Attend 5 different events', 'events', 5, '📅');

-- Award some badges to sample volunteers
INSERT INTO Award (volunteer_id, award_criteria_id, awarded_date, awarded_by) VALUES
(1, 1, '2025-11-15 10:00:00', 1),
(1, 2, '2025-11-28 14:30:00', 1),
(3, 1, '2025-11-10 09:00:00', 1),
(3, 2, '2025-11-25 11:00:00', 1),
(4, 1, '2025-11-05 16:00:00', 1),
(4, 2, '2025-11-20 13:00:00', 1),
(4, 3, '2025-11-29 15:00:00', 1);

COMMIT;
