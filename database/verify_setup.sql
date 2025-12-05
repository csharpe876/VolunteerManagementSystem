-- Quick database verification script
-- Run this in phpMyAdmin SQL tab to check if everything is set up correctly

-- Check if database exists
SELECT 'Database exists' as Status;

-- Check if SystemAdmin table exists and has data
SELECT COUNT(*) as AdminCount FROM SystemAdmin;

-- Check admin user details
SELECT admin_id, username, email, first_name, last_name, role, account_status 
FROM SystemAdmin 
WHERE username = 'admin';

-- Check if password hash exists
SELECT 
    CASE 
        WHEN LENGTH(password_hash) > 0 THEN 'Password hash exists'
        ELSE 'Password hash MISSING'
    END as PasswordStatus
FROM SystemAdmin 
WHERE username = 'admin';

-- Check Volunteer table
SELECT COUNT(*) as VolunteerCount FROM Volunteer;

-- Check specific volunteer
SELECT volunteer_id, email, first_name, last_name, is_active
FROM Volunteer
WHERE email = 'carl.sharpe@mymona.uwi.edu';
