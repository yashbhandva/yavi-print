package com.web_app.yaviPrint.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends BusinessException {
    private final String resourceName;
    private final Object identifier;

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s not found with id: %s", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object value) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, value));
        this.resourceName = resourceName;
        this.identifier = value;
    }
}