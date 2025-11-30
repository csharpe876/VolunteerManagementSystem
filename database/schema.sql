-- Create Database
CREATE DATABASE IF NOT EXISTS volunteer_management_system;
USE volunteer_management_system;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS AuditLog;
DROP TABLE IF EXISTS Award;
DROP TABLE IF EXISTS AwardCriteria;
DROP TABLE IF EXISTS Timesheet;
DROP TABLE IF EXISTS Attendance;
DROP TABLE IF EXISTS Announcement;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS SystemAdmin;
DROP TABLE IF EXISTS Volunteer;

-- Volunteer Table
CREATE TABLE Volunteer (
    volunteer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('active', 'inactive') DEFAULT 'active',
    profile_photo_url VARCHAR(255),
    date_of_birth DATE,
    address TEXT,
    password_hash VARCHAR(255),
    INDEX idx_email (email),
    INDEX idx_status (status)
);

-- SystemAdmin Table
CREATE TABLE SystemAdmin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('super_admin', 'coordinator') NOT NULL,
    permissions JSON,
    last_login TIMESTAMP NULL,
    failed_login_attempts INT DEFAULT 0,
    account_status ENUM('active', 'inactive', 'locked') DEFAULT 'active',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT,
    INDEX idx_username (username),
    INDEX idx_role (role)
);

-- Event Table
CREATE TABLE Event (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    current_registrations INT DEFAULT 0,
    event_type ENUM('workshop', 'meeting', 'community_service', 'social') NOT NULL,
    status ENUM('draft', 'published', 'cancelled', 'completed') DEFAULT 'draft',
    created_by INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES SystemAdmin(admin_id) ON DELETE SET NULL,
    INDEX idx_event_date (event_date),
    INDEX idx_status (status),
    INDEX idx_event_type (event_type)
);

-- Attendance Table
CREATE TABLE Attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    event_id INT NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP NULL,
    status ENUM('present', 'absent', 'late', 'excused') DEFAULT 'present',
    hours_worked DECIMAL(5,2),
    notes TEXT,
    recorded_by INT,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES SystemAdmin(admin_id) ON DELETE SET NULL,
    UNIQUE KEY unique_volunteer_event (volunteer_id, event_id),
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_event (event_id)
);

-- Timesheet Table
CREATE TABLE Timesheet (
    timesheet_id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    total_hours DECIMAL(6,2),
    approved_hours DECIMAL(6,2) NULL,
    approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    approved_by INT,
    approval_date TIMESTAMP NULL,
    rejection_reason TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES SystemAdmin(admin_id) ON DELETE SET NULL,
    UNIQUE KEY unique_volunteer_period (volunteer_id, period_start_date),
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_status (approval_status)
);

-- Announcement Table
CREATE TABLE Announcement (
    announcement_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    published_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP NULL,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    target_audience ENUM('all', 'coordinators', 'volunteers') DEFAULT 'all',
    event_id INT,
    created_by INT,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES SystemAdmin(admin_id) ON DELETE SET NULL,
    INDEX idx_expiry (expiry_date),
    INDEX idx_priority (priority)
);

-- AwardCriteria Table
CREATE TABLE AwardCriteria (
    criteria_id INT AUTO_INCREMENT PRIMARY KEY,
    badge_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    criteria_type ENUM('total_hours', 'event_count', 'consecutive_months', 'special_achievement') NOT NULL,
    threshold_value INT,
    badge_tier ENUM('bronze', 'silver', 'gold', 'platinum') NOT NULL,
    badge_icon_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Award Table
CREATE TABLE Award (
    award_id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    badge_name VARCHAR(100) NOT NULL,
    badge_description TEXT,
    criteria_id INT,
    date_earned TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    badge_tier ENUM('bronze', 'silver', 'gold', 'platinum') NOT NULL,
    badge_icon_url VARCHAR(255),
    is_featured BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id) ON DELETE CASCADE,
    FOREIGN KEY (criteria_id) REFERENCES AwardCriteria(criteria_id) ON DELETE SET NULL,
    UNIQUE KEY unique_volunteer_criteria (volunteer_id, criteria_id),
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_date_earned (date_earned)
);

-- AuditLog Table
CREATE TABLE AuditLog (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    user_type ENUM('admin', 'volunteer') NOT NULL,
    action_type ENUM('create', 'update', 'delete', 'login', 'logout') NOT NULL,
    table_name VARCHAR(50),
    record_id INT,
    old_value JSON,
    new_value JSON,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id, user_type),
    INDEX idx_action (action_type),
    INDEX idx_table (table_name),
    INDEX idx_timestamp (timestamp)
);

-- Insert Sample Award Criteria
INSERT INTO AwardCriteria (badge_name, description, criteria_type, threshold_value, badge_tier) VALUES
('First Steps', 'Complete your first 10 hours of volunteering', 'total_hours', 10, 'bronze'),
('Dedicated Volunteer', 'Reach 50 hours of community service', 'total_hours', 50, 'silver'),
('Century Club', 'Achieve 100 hours of volunteer work', 'total_hours', 100, 'gold'),
('Legend Status', 'Accomplish 200+ hours of volunteering', 'total_hours', 200, 'platinum'),
('Event Enthusiast', 'Participate in 10 different events', 'event_count', 10, 'bronze'),
('Consistent Contributor', 'Volunteer for 3 consecutive months', 'consecutive_months', 3, 'silver');

-- Insert Sample Admin
INSERT INTO SystemAdmin (username, password_hash, email, first_name, last_name, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@fstgc.uwi.edu', 'System', 'Admin', 'super_admin');
-- Password is 'admin123' (should be changed in production)