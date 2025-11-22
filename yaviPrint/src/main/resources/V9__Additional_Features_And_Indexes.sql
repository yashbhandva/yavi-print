-- V9__Additional_Features_And_Indexes.sql
-- Additional features and performance indexes

-- Shop verification table
CREATE TABLE shop_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_date DATETIME,
    verified_by VARCHAR(255),
    documents_submitted TEXT,
    rejection_reason TEXT,
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    UNIQUE KEY unique_shop_id (shop_id),
    INDEX idx_shop_id (shop_id),
    INDEX idx_is_verified (is_verified)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Shop devices table
CREATE TABLE shop_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    device_type VARCHAR(100),
    mac_address VARCHAR(17) UNIQUE,
    ip_address VARCHAR(45),
    is_active BOOLEAN DEFAULT TRUE,
    last_seen DATETIME DEFAULT CURRENT_TIMESTAMP,
    printer_name VARCHAR(255),
    printer_model VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    INDEX idx_shop_id (shop_id),
    INDEX idx_mac_address (mac_address),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Shop status table
CREATE TABLE shop_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    is_online BOOLEAN DEFAULT FALSE,
    is_busy BOOLEAN DEFAULT FALSE,
    current_queue_size INT DEFAULT 0,
    max_queue_size INT DEFAULT 10,
    busy_reason TEXT,
    status_updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    UNIQUE KEY unique_shop_id (shop_id),
    INDEX idx_shop_id (shop_id),
    INDEX idx_online_busy (is_online, is_busy)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Geo location table
CREATE TABLE geo_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    formatted_address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'India',
    accuracy DECIMAL(5, 2),
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    UNIQUE KEY unique_shop_id (shop_id),
    INDEX idx_shop_id (shop_id),
    SPATIAL INDEX idx_location (latitude, longitude),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Print job history table
CREATE TABLE print_job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    performed_by VARCHAR(255),
    action_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    old_value TEXT,
    new_value TEXT,

    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_action_time (action_time),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Page range selections table
CREATE TABLE page_range_selections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    start_page INT NOT NULL,
    end_page INT NOT NULL,
    is_range BOOLEAN DEFAULT TRUE,
    copies INT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Print settings table
CREATE TABLE print_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_size ENUM('A4', 'A3', 'PHOTO') DEFAULT 'A4',
    print_type ENUM('BW', 'COLOR') DEFAULT 'BW',
    is_duplex BOOLEAN DEFAULT FALSE,
    page_ranges VARCHAR(500),
    copies INT DEFAULT 1,
    orientation ENUM('PORTRAIT', 'LANDSCAPE') DEFAULT 'PORTRAIT',
    quality ENUM('DRAFT', 'STANDARD', 'HIGH') DEFAULT 'STANDARD',
    is_fit_to_page BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Performance indexes
CREATE INDEX idx_orders_composite ON orders(shop_id, status, created_at);
CREATE INDEX idx_payments_composite ON payments(status, payment_date);
CREATE INDEX idx_users_composite ON users(is_enabled, created_at);
CREATE INDEX idx_shops_location_composite ON shops(city, is_online, is_active);
CREATE INDEX idx_reviews_composite ON shop_reviews(shop_id, review_date);
CREATE INDEX idx_notifications_composite ON notifications(user_id, is_read, sent_at);

-- Full-text search indexes (if needed)
-- CREATE FULLTEXT INDEX ft_shop_name ON shops(shop_name, description);
-- CREATE FULLTEXT INDEX ft_user_search ON users(name, email);