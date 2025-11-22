package com.web_app.yaviPrint.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, Object> details;

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.details = null;
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.details = null;
    }

    public BusinessException(String message, HttpStatus status, Map<String, Object> details) {
        super(message);
        this.status = status;
        this.details = details;
    }
}