-- Quick Database Setup for Volunteer Management System
-- Run this in phpMyAdmin SQL tab after selecting the database

-- First, check if data exists
SELECT 'Checking existing data...' as Status;
SELECT COUNT(*) as AdminCount FROM SystemAdmin;
SELECT COUNT(*) as VolunteerCount FROM Volunteer;

-- If counts are 0, insert the data below:

-- Insert System Administrators
INSERT INTO SystemAdmin (username, password_hash, email, first_name, last_name, role, account_status) VALUES
('admin', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', 'admin@fstgc.uwi.edu', 'System', 'Administrator', 'super_admin', 'active'),
('coordinator', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', 'coordinator@fstgc.uwi.edu', 'Event', 'Coordinator', 'coordinator', 'active');
-- Password for both: admin123

-- Insert Sample Volunteers
INSERT INTO Volunteer (first_name, last_name, email, phone, password_hash, date_of_birth, address, skills, is_active, total_hours) VALUES
('Carl', 'Sharpe', 'carl.sharpe@mymona.uwi.edu', '876-555-0101', '$2a$10$8K1p/a0dL3.Ry0.Y5V0nOeqYYPnM5.FxTvQGcH1Iu6K8g9v5SqQRG', '2003-05-15', 'Kingston, Jamaica', 'Programming, Event Planning', TRUE, 25.5);
-- Password: volunteer123

-- Verify the data was inserted
SELECT 'Verification - Admin exists:' as Check, COUNT(*) as Count FROM SystemAdmin WHERE username = 'admin';
SELECT 'Verification - Admin details:' as Check, username, email FROM SystemAdmin WHERE username = 'admin';
