-- V1__Initial_Schema_Setup.sql
-- Initial database schema for YaviPrint application

-- Create database schema if not exists
CREATE DATABASE IF NOT EXISTS `${app_schema}` CHARACTER SET ${default_charset} COLLATE ${default_collation};
USE `${app_schema}`;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'SHOP_OWNER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    is_enabled BOOLEAN DEFAULT FALSE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    verification_token VARCHAR(255),
    verification_token_expiry DATETIME,
    verified_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_verification_token (verification_token),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- User profiles table
CREATE TABLE user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    profile_picture VARCHAR(500),
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    alternate_phone VARCHAR(20),
    preferences JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_id (user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- User login history table
CREATE TABLE user_login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_type ENUM('DESKTOP', 'MOBILE', 'TABLET'),
    success BOOLEAN DEFAULT TRUE,
    failure_reason TEXT,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_success (success)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};