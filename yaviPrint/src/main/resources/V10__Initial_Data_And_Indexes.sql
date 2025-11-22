-- V10__Initial_Data_And_Indexes.sql
-- Initial data setup and final indexes

-- Insert default system settings
INSERT INTO system_settings (setting_key, setting_value, data_type, category, description, is_editable) VALUES
('app.name', 'YaviPrint', 'STRING', 'GENERAL', 'Application name', false),
('app.version', '1.0.0', 'STRING', 'GENERAL', 'Application version', false),
('app.maintenance', 'false', 'BOOLEAN', 'GENERAL', 'Maintenance mode', true),
('payment.platform.fee', '0.10', 'NUMBER', 'PAYMENT', 'Platform commission fee', true),
('payment.currency', 'INR', 'STRING', 'PAYMENT', 'Default currency', false),
('notification.email.enabled', 'true', 'BOOLEAN', 'NOTIFICATION', 'Email notifications enabled', true),
('notification.sms.enabled', 'false', 'BOOLEAN', 'NOTIFICATION', 'SMS notifications enabled', true),
('file.max.size', '10485760', 'NUMBER', 'FILE', 'Maximum file size in bytes', true),
('file.allowed.types', 'PDF,DOC,DOCX,JPG,JPEG,PNG', 'STRING', 'FILE', 'Allowed file types', true),
('order.pickup.expiry.hours', '72', 'NUMBER', 'ORDER', 'Pickup code expiry in hours', true),
('order.auto.cancel.hours', '24', 'NUMBER', 'ORDER', 'Auto cancel pending orders after hours', true),
('shop.verification.required', 'true', 'BOOLEAN', 'SHOP', 'Shop verification required', true),
('default.bw.price', '1.00', 'NUMBER', 'PRICING', 'Default BW print price per page', true),
('default.color.price', '3.00', 'NUMBER', 'PRICING', 'Default color print price per page', true);

-- Insert default support categories
INSERT INTO support_categories (name, description, department, sla_hours, is_active) VALUES
('TECHNICAL_ISSUE', 'Technical problems with the app or website', 'TECHNICAL', 12, true),
('PAYMENT_ISSUE', 'Problems with payments or refunds', 'BILLING', 6, true),
('PRINT_QUALITY', 'Issues with print quality or output', 'QUALITY', 24, true),
('ORDER_STATUS', 'Questions about order status or tracking', 'ORDER', 12, true),
('ACCOUNT_ISSUE', 'Problems with user account or login', 'ACCOUNT', 6, true),
('SHOP_REGISTRATION', 'Questions about shop registration or verification', 'SHOP', 48, true),
('GENERAL_INQUIRY', 'General questions or feedback', 'GENERAL', 24, true);

-- Insert default notification templates
INSERT INTO notification_templates (template_name, template_type, subject, content, variables, is_active, language) VALUES
('EMAIL_VERIFICATION', 'EMAIL', 'Verify Your YaviPrint Account',
 'Hello {{name}},\n\nPlease click the link below to verify your email address:\n{{verificationUrl}}\n\nThis link will expire in 24 hours.\n\nThank you for choosing YaviPrint!',
 '{"name": "User name", "verificationUrl": "Verification URL"}', true, 'en'),

('ORDER_CONFIRMATION', 'EMAIL', 'YaviPrint Order Confirmation - Token: {{tokenId}}',
 'Hello {{name}},\n\nYour order has been confirmed!\nOrder Token: {{tokenId}}\n\nYou can track your order status in the app.\n\nThank you for choosing YaviPrint!',
 '{"name": "User name", "tokenId": "Order token ID"}', true, 'en'),

('ORDER_READY', 'PUSH', 'Your Order is Ready for Pickup',
 'Your order {{tokenId}} is ready for pickup at {{shopName}}.',
 '{"tokenId": "Order token ID", "shopName": "Shop name"}', true, 'en'),

('PAYMENT_SUCCESS', 'EMAIL', 'Payment Successful - Order {{tokenId}}',
 'Hello {{name}},\n\nYour payment of ₹{{amount}} for order {{tokenId}} has been successfully processed.\n\nThank you for your purchase!',
 '{"name": "User name", "amount": "Payment amount", "tokenId": "Order token ID"}', true, 'en');

-- Create default admin user (password: admin123 - should be changed after first login)
INSERT INTO admin_users (username, email, password, role, is_active, permissions) VALUES
('superadmin', 'admin@yaviprint.com', '$2a$12$Kq9Z9b8s7c6d5e4f3g2h1.iJkLmNoPqRsTuVwXyZzAbCdEfGhIjKl', 'SUPER_ADMIN', true, '["ALL"]');

-- Create performance views (optional)
CREATE VIEW shop_performance AS
SELECT
    s.id,
    s.shop_name,
    s.city,
    s.rating,
    s.total_reviews,
    COUNT(o.id) as total_orders,
    SUM(CASE WHEN o.status = ''COMPLETED'' THEN 1 ELSE 0 END) as completed_orders,
    AVG(CASE WHEN o.status = ''COMPLETED'' THEN o.total_amount ELSE NULL END) as avg_order_value
FROM shops s
LEFT JOIN orders o ON s.id = o.shop_id
WHERE s.is_active = true
GROUP BY s.id, s.shop_name, s.city, s.rating, s.total_reviews;

-- Create revenue summary view
CREATE VIEW revenue_summary AS
SELECT
    DATE(p.payment_date) as payment_date,
    COUNT(p.id) as total_transactions,
    SUM(p.amount) as total_revenue,
    AVG(p.amount) as avg_transaction_value
FROM payments p
WHERE p.status = ''CAPTURED''
GROUP BY DATE(p.payment_date);

-- Final composite indexes for query performance
CREATE INDEX idx_orders_user_date ON orders(user_id, created_at);
CREATE INDEX idx_orders_shop_date ON orders(shop_id, created_at);
CREATE INDEX idx_payments_date_status ON payments(payment_date, status);
CREATE INDEX idx_users_created_enabled ON users(created_at, is_enabled);
CREATE INDEX idx_shops_city_rating ON shops(city, rating);
CREATE INDEX idx_reviews_shop_rating ON shop_reviews(shop_id, rating);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read, sent_at);

-- Add comments to tables for documentation
ALTER TABLE users COMMENT = 'Stores user accounts and authentication information';
ALTER TABLE shops COMMENT = 'Stores printing shop information and settings';
ALTER TABLE orders COMMENT = 'Stores print orders and their status';
ALTER TABLE payments COMMENT = 'Stores payment transactions and status';
ALTER TABLE shop_reviews COMMENT = 'Stores customer reviews and ratings for shops';
ALTER TABLE notifications COMMENT = 'Stores user notifications and alerts';

-- Update table comments
ALTER TABLE document_files COMMENT = 'Stores uploaded document files information';
ALTER TABLE print_pickup_codes COMMENT = 'Stores QR codes and pickup information for orders';
ALTER TABLE wallet_transactions COMMENT = 'Stores wallet transactions for users';
ALTER TABLE support_tickets COMMENT = 'Stores customer support tickets';
ALTER TABLE admin_users COMMENT = 'Stores admin user accounts';
ALTER TABLE system_settings COMMENT = 'Stores application configuration settings';