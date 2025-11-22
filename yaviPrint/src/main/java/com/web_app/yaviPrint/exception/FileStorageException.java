package com.web_app.yaviPrint.exception;

public class FileStorageException extends BusinessException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message + ": " + cause.getMessage());
    }
}