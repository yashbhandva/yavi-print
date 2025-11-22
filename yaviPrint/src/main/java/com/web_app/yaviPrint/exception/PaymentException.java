package com.web_app.yaviPrint.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class PaymentException extends BusinessException {
    public PaymentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public PaymentException(String message, Map<String, Object> details) {
        super(message, HttpStatus.BAD_REQUEST, details);
    }
}