-- ===================================================================
-- CampusPass — Initial System Seed Data Script
-- ===================================================================

USE campuspass_db;

-- Seed Default Departments
INSERT INTO departments (code, name, description, is_active) VALUES
('CSE', 'Computer Science & Engineering', 'Department of Computer Science and Engineering', TRUE),
('IT', 'Information Technology', 'Department of Information Technology', TRUE),
('ECE', 'Electronics & Communication', 'Department of Electronics & Communication Engineering', TRUE),
('MECH', 'Mechanical Engineering', 'Department of Mechanical Engineering', TRUE),
('CIVIL', 'Civil Engineering', 'Department of Civil Engineering', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Seed Default System Settings
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('GRACE_PERIOD_MINUTES', '15', 'Allowed grace period in minutes after expected return time before marking overdue'),
('MAX_PASS_DURATION_HOURS', '48', 'Maximum duration in hours for a single student permission pass'),
('EMERGENCY_NOTIFICATION_ENABLED', 'true', 'Flag to trigger immediate staff notification on emergency requests'),
('COLLEGE_NAME', 'Engineering & Technology Campus', 'Institutional display name for passes and banners')
ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value);
