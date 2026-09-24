-- ===================================================================
-- CampusPass — Digital Student Permission & Gate Movement System
-- Schema DDL Script (MySQL 8.x / InnoDB)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS campuspass_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE campuspass_db;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Base Users Table (Authentication Credentials & Roles)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('STUDENT', 'HOD', 'SECURITY', 'ADMIN') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Students Table (Student-specific Profile)
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    semester INT NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    parent_name VARCHAR(100) NOT NULL,
    parent_phone VARCHAR(15) NOT NULL,
    student_photo_url VARCHAR(255),
    id_card_doc_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_students_department FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX idx_students_roll (roll_number),
    INDEX idx_students_dept (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. HODs Table (Department Heads)
CREATE TABLE IF NOT EXISTS hods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    office_room VARCHAR(50),
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_hods_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_hods_department FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX idx_hods_dept (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Security Users Table (Gate Security Personnel)
CREATE TABLE IF NOT EXISTS security_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    badge_id VARCHAR(50) NOT NULL UNIQUE,
    gate_number VARCHAR(30) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_security_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Applications Table (Permission Requests)
CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_number VARCHAR(30) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    subject VARCHAR(200) NOT NULL,
    reason_text TEXT NOT NULL,
    is_emergency BOOLEAN NOT NULL DEFAULT FALSE,
    leave_date DATE NOT NULL,
    leave_time TIME NOT NULL,
    expected_return_date_time DATETIME NOT NULL,
    status ENUM('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'CLARIFICATION_REQUIRED', 'RESUBMITTED', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'SUBMITTED',
    hod_remarks TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_app_department FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX idx_app_student (student_id),
    INDEX idx_app_dept_status (department_id, status),
    INDEX idx_app_status (status),
    INDEX idx_app_number (application_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Application Documents (Attached Evidence/Letters)
CREATE TABLE IF NOT EXISTS application_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_docs_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    INDEX idx_docs_application (application_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. E-Passes Table (Digital Movement Passes)
CREATE TABLE IF NOT EXISTS epasses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pass_number VARCHAR(30) NOT NULL UNIQUE,
    application_id BIGINT NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    qr_token VARCHAR(128) NOT NULL UNIQUE,
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'USED', 'REVOKED', 'COMPLETED') NOT NULL DEFAULT 'ACTIVE',
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at DATETIME NULL,
    CONSTRAINT fk_epass_application FOREIGN KEY (application_id) REFERENCES applications(id),
    CONSTRAINT fk_epass_student FOREIGN KEY (student_id) REFERENCES students(id),
    INDEX idx_epass_qr (qr_token),
    INDEX idx_epass_status (status),
    INDEX idx_epass_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Gate Movements Table (Exit & Return Records)
CREATE TABLE IF NOT EXISTS gate_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pass_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    exit_security_id BIGINT NOT NULL,
    exit_time DATETIME NOT NULL,
    expected_return_time DATETIME NOT NULL,
    return_security_id BIGINT NULL,
    actual_return_time DATETIME NULL,
    movement_status ENUM('OUTSIDE', 'RETURNED_ON_TIME', 'OVERDUE', 'RETURNED_OVERDUE') NOT NULL DEFAULT 'OUTSIDE',
    gate_remarks TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_gm_pass FOREIGN KEY (pass_id) REFERENCES epasses(id),
    CONSTRAINT fk_gm_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_gm_exit_sec FOREIGN KEY (exit_security_id) REFERENCES security_users(id),
    CONSTRAINT fk_gm_ret_sec FOREIGN KEY (return_security_id) REFERENCES security_users(id),
    INDEX idx_gm_pass (pass_id),
    INDEX idx_gm_status (movement_status),
    INDEX idx_gm_student (student_id),
    INDEX idx_gm_expected_return (expected_return_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. Pass Extensions Table (Student Return Time Extensions)
CREATE TABLE IF NOT EXISTS pass_extensions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pass_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    requested_return_time DATETIME NOT NULL,
    reason TEXT NOT NULL,
    hod_id BIGINT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    hod_remarks TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ext_pass FOREIGN KEY (pass_id) REFERENCES epasses(id),
    CONSTRAINT fk_ext_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_ext_hod FOREIGN KEY (hod_id) REFERENCES hods(id),
    INDEX idx_ext_pass (pass_id),
    INDEX idx_ext_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. In-App Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user_unread (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Audit Logs Table (Append-Only Event Ledger)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_user_id BIGINT NULL,
    actor_username VARCHAR(50) NOT NULL,
    actor_role VARCHAR(30) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    ip_address VARCHAR(45) NULL,
    details TEXT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_actor (actor_user_id),
    INDEX idx_audit_entity (entity_name, entity_id),
    INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. System Settings Table (Institutional Configuration)
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(50) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
