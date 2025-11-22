-- V8__Analytics_And_Additional_Tables.sql
-- Analytics and additional feature tables

-- Daily shop stats table
CREATE TABLE daily_shop_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    total_orders INT DEFAULT 0,
    completed_orders INT DEFAULT 0,
    cancelled_orders INT DEFAULT 0,
    total_revenue DECIMAL(12, 2) DEFAULT 0.00,
    platform_earnings DECIMAL(12, 2) DEFAULT 0.00,
    new_customers INT DEFAULT 0,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    page_views INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    UNIQUE KEY unique_shop_date (shop_id, stat_date),
    INDEX idx_shop_id (shop_id),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Daily user stats table
CREATE TABLE daily_user_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL,
    new_registrations INT DEFAULT 0,
    active_users INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    returning_users INT DEFAULT 0,
    total_revenue DECIMAL(12, 2) DEFAULT 0.00,
    device_breakdown JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY unique_stat_date (stat_date),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Daily revenue table
CREATE TABLE daily_revenue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    revenue_date DATE NOT NULL,
    total_revenue DECIMAL(12, 2) DEFAULT 0.00,
    platform_commission DECIMAL(12, 2) DEFAULT 0.00,
    shop_earnings DECIMAL(12, 2) DEFAULT 0.00,
    tax_amount DECIMAL(12, 2) DEFAULT 0.00,
    total_transactions INT DEFAULT 0,
    refund_amount DECIMAL(12, 2) DEFAULT 0.00,
    payment_method_breakdown JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY unique_revenue_date (revenue_date),
    INDEX idx_revenue_date (revenue_date)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Print volume stats table
CREATE TABLE print_volume_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL,
    total_pages_printed INT DEFAULT 0,
    bw_pages INT DEFAULT 0,
    color_pages INT DEFAULT 0,
    a4_pages INT DEFAULT 0,
    a3_pages INT DEFAULT 0,
    photo_pages INT DEFAULT 0,
    hourly_distribution JSON,
    popular_time_slots JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY unique_stat_date (stat_date),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Support categories table
CREATE TABLE support_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    department VARCHAR(100) DEFAULT 'GENERAL',
    sla_hours INT DEFAULT 24,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_department (department),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Notification templates table
CREATE TABLE notification_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(255) UNIQUE NOT NULL,
    template_type VARCHAR(100) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    variables JSON,
    is_active BOOLEAN DEFAULT TRUE,
    language VARCHAR(10) DEFAULT 'en',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_template_type (template_type),
    INDEX idx_is_active (is_active),
    INDEX idx_language (language)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};