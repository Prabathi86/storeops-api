package com.storeops.common;

public class NotFoundError extends AppError {
    public NotFoundError(String code, String message) {
        super(code, message, 404);
    }
}