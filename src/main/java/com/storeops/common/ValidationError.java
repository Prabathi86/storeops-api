package com.storeops.common;

public class ValidationError extends AppError {
    public ValidationError(String code, String message) {
        super(code, message, 400);
    }
}
