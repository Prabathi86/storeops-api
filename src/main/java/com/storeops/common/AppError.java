package com.storeops.common;

public class AppError extends RuntimeException {
    private final String code;
    private final int statusCode;

    public AppError(String code, String message, int statusCode) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }

    public String getCode() { return code; }
    public int getStatusCode() { return statusCode; }
}
