package com.web_app.yaviPrint.exception;

public class ExceptionConstants {

    // User exceptions
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists with this email";
    public static final String EMAIL_ALREADY_VERIFIED = "Email already verified";
    public static final String INVALID_VERIFICATION_TOKEN = "Invalid verification token";
    public static final String VERIFICATION_TOKEN_EXPIRED = "Verification token expired";

    // Shop exceptions
    public static final String SHOP_NOT_FOUND = "Shop not found";
    public static final String SHOP_ALREADY_EXISTS = "User already has a shop";
    public static final String SHOP_NOT_VERIFIED = "Shop is not verified";
    public static final String SHOP_OFFLINE = "Shop is currently offline";

    // Order exceptions
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_CANNOT_BE_CANCELLED = "Order cannot be cancelled in current status";
    public static final String INVALID_ORDER_STATUS = "Invalid order status transition";

    // File exceptions
    public static final String FILE_NOT_FOUND = "File not found";
    public static final String FILE_TOO_LARGE = "File size exceeds maximum limit";
    public static final String UNSUPPORTED_FILE_TYPE = "Unsupported file type";
    public static final String FILE_UPLOAD_FAILED = "File upload failed";

    // Payment exceptions
    public static final String PAYMENT_NOT_FOUND = "Payment not found";
    public static final String PAYMENT_ALREADY_PROCESSED = "Payment already processed";
    public static final String INSUFFICIENT_WALLET_BALANCE = "Insufficient wallet balance";
    public static final String PAYMENT_GATEWAY_ERROR = "Payment gateway error";

    // Review exceptions
    public static final String REVIEW_NOT_FOUND = "Review not found";
    public static final String ALREADY_REVIEWED = "You have already reviewed this order";

    // Support exceptions
    public static final String TICKET_NOT_FOUND = "Support ticket not found";
    public static final String TICKET_ALREADY_CLOSED = "Ticket is already closed";

    private ExceptionConstants() {
        // Utility class
    }
}