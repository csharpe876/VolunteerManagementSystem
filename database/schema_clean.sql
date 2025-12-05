-- Volunteer Management System Database Schema
-- Import this file FIRST before sample_data.sql
-- For phpMyAdmin: Select the database first, then import this file

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS AuditLog;
DROP TABLE IF EXISTS Award;
DROP TABLE IF EXISTS AwardCriteria;
DROP TABLE IF EXISTS Timesheet;
DROP TABLE IF EXISTS Attendance;
DROP TABLE IF EXISTS Announcement;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS SystemAdmin;
DROP TABLE IF EXISTS Volunteer;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Volunteer Table
CREATE TABLE Volunteer (
    volunteer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    profile_photo_url VARCHAR(255),
    date_of_birth DATE,
    address TEXT,
    password_hash VARCHAR(255) NOT NULL,
    skills TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    last_login TIMESTAMP NULL,
    total_hours DECIMAL(10,2) DEFAULT 0.00,
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- System Admin Table
CREATE TABLE SystemAdmin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('super_admin', 'admin', 'coordinator') DEFAULT 'admin',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    account_status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    phone VARCHAR(20),
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Event Table
CREATE TABLE Event (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    location VARCHAR(200),
    capacity INT,
    registered_count INT DEFAULT 0,
    status ENUM('draft', 'published', 'cancelled', 'completed') DEFAULT 'draft',
    created_by INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES SystemAdmin(admin_id) ON DELETE CASCADE,
    INDEX idx_event_date (event_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Attendance Table
CREATE TABLE Attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    volunteer_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    attendance_status ENUM('registered', 'attended', 'absent', 'cancelled') DEFAULT 'registered',
    check_in_time TIMESTAMP NULL,
    check_out_time TIMESTAMP NULL,
    notes TEXT,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (event_id, volunteer_id),
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Timesheet Table
CREATE TABLE Timesheet (
    timesheet_id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    event_id INT,
    activity_description TEXT NOT NULL,
    activity_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    hours_worked DECIMAL(5,2) NOT NULL,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    approved_by INT,
    approved_date TIMESTAMP NULL,
    submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES SystemAdmin(admin_id) ON DELETE SET NULL,
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_status (status),
    INDEX idx_activity_date (activity_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Award Criteria Table
CREATE TABLE AwardCriteria (
    criteria_id INT AUTO_INCREMENT PRIMARY KEY,
    award_name VARCHAR(100) NOT NULL,
    description TEXT,
    criteria_type ENUM('hours', 'events', 'special') NOT NULL,
    threshold_value DECIMAL(10,2),
    badge_icon VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Award Table
CREATE TABLE Award (
    award_id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    award_criteria_id INT NOT NULL,
    awarded_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    awarded_by INT NOT NULL,
    notes TEXT,
    FOREIGN KEY (volunteer_id) REFERENCES Volunteer(volunteer_id) ON DELETE CASCADE,
    FOREIGN KEY (award_criteria_id) REFERENCES AwardCriteria(criteria_id) ON DELETE CASCADE,
    FOREIGN KEY (awarded_by) REFERENCES SystemAdmin(admin_id) ON DELETE CASCADE,
    UNIQUE KEY unique_award (volunteer_id, award_criteria_id),
    INDEX idx_volunteer (volunteer_id),
    INDEX idx_criteria (award_criteria_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Announcement Table
CREATE TABLE Announcement (
    announcement_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    priority ENUM('low', 'medium', 'high') DEFAULT 'medium',
    target_audience ENUM('all', 'volunteers', 'admins') DEFAULT 'all',
    status ENUM('draft', 'published', 'archived') DEFAULT 'draft',
    posted_by INT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP NULL,
    FOREIGN KEY (posted_by) REFERENCES SystemAdmin(admin_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_posted_date (posted_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Audit Log Table
CREATE TABLE AuditLog (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    user_type ENUM('admin', 'volunteer') NOT NULL,
    action VARCHAR(100) NOT NULL,
    table_affected VARCHAR(50),
    record_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    details TEXT,
    INDEX idx_user (user_id, user_type),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
