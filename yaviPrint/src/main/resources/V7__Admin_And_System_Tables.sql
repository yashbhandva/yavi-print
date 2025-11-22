-- V7__Admin_And_System_Tables.sql
-- Admin and system management tables

-- Admin users table
CREATE TABLE admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    permissions JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Admin action logs table
CREATE TABLE admin_action_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    target_entity VARCHAR(100),
    target_id BIGINT,
    ip_address VARCHAR(45),
    action_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    old_values JSON,
    new_values JSON,

    FOREIGN KEY (admin_user_id) REFERENCES admin_users(id) ON DELETE CASCADE,
    INDEX idx_admin_user_id (admin_user_id),
    INDEX idx_action_time (action_time),
    INDEX idx_target_entity (target_entity, target_id),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- System settings table
CREATE TABLE system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(255) UNIQUE NOT NULL,
    setting_value TEXT NOT NULL,
    data_type ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    category VARCHAR(100) DEFAULT 'GENERAL',
    description TEXT,
    is_editable BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_setting_key (setting_key),
    INDEX idx_category (category),
    INDEX idx_is_editable (is_editable)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Shop reports table
CREATE TABLE shop_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    reported_by BIGINT NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    admin_notes TEXT,
    reported_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    FOREIGN KEY (reported_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_shop_id (shop_id),
    INDEX idx_reported_by (reported_by),
    INDEX idx_status (status),
    INDEX idx_reported_at (reported_at)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- User reports table
CREATE TABLE user_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reported_user_id BIGINT NOT NULL,
    reported_by BIGINT NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    reported_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reported_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_reported_user_id (reported_user_id),
    INDEX idx_reported_by (reported_by),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};