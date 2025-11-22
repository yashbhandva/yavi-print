package com.web_app.yaviPrint.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateResourceException extends BusinessException {
    private final String resourceName;

    public DuplicateResourceException(String resourceName, String message) {
        super(message, HttpStatus.CONFLICT);
        this.resourceName = resourceName;
    }
}