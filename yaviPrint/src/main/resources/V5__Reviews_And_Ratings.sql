-- V5__Reviews_And_Ratings.sql
-- Reviews and ratings tables

-- Shop reviews table
CREATE TABLE shop_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    helpful_count INT DEFAULT 0,
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at DATETIME,

    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    UNIQUE KEY unique_order_review (order_id),
    INDEX idx_shop_id (shop_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating),
    INDEX idx_review_date (review_date),
    INDEX idx_verified_purchase (is_verified_purchase)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};

-- Review replies table
CREATE TABLE review_replies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    shop_owner_id BIGINT NOT NULL,
    reply_message TEXT NOT NULL,
    reply_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at DATETIME,

    FOREIGN KEY (review_id) REFERENCES shop_reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (shop_owner_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_review_id (review_id),
    INDEX idx_review_id (review_id),
    INDEX idx_shop_owner_id (shop_owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=${default_charset} COLLATE=${default_collation};