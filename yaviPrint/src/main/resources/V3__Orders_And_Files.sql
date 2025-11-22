-- V3__Orders_And_Files.sql
-- Orders and file management tables

-- Document files table
CREATE TABLE document_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(500) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    file_hash VARCHAR(255),
    total_pages INT DEFAULT 0,
    is_compressed BOOLEAN DEFAULT FALSE,
    uploaded_by BIGINT NOT NULL,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME,

    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_uploaded_by (uploaded_by),
    INDEX idx_file_hash (file_hash),
    INDEX idx_expires_at (expires_at),
    INDEX idx_uploaded_at (uploaded_at)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    token_id VARCHAR(50) UNIQUE NOT NULL,
    qr_code_url VARCHAR(1000),
    file_name VARCHAR(500) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    total_pages INT NOT NULL,
    print_settings JSON,
    total_amount DECIMAL(10, 2) NOT NULL,
    platform_fee DECIMAL(10, 2) DEFAULT 0.00,
    status ENUM('PENDING', 'ACCEPTED', 'PRINTING', 'READY', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    payment_status BOOLEAN DEFAULT FALSE,
    payment_id VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    printed_at DATETIME,
    collected_at DATETIME,
    cancelled_at DATETIME,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_shop_id (shop_id),
    INDEX idx_token_id (token_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at),
    INDEX idx_shop_status (shop_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Print pickup codes table
CREATE TABLE print_pickup_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    token_id VARCHAR(50) NOT NULL,
    qr_code_url VARCHAR(1000),
    short_code VARCHAR(10) UNIQUE NOT NULL,
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    used_at DATETIME,

    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    UNIQUE KEY unique_order_id (order_id),
    INDEX idx_token_id (token_id),
    INDEX idx_short_code (short_code),
    INDEX idx_expires_at (expires_at),
    INDEX idx_used (is_used)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Print job status table
CREATE TABLE print_job_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(100) NOT NULL,
    message TEXT,
    progress INT DEFAULT 0,
    status_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_status_time (status_time)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};