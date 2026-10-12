package com.storeops.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppError.class)
    public ResponseEntity<Map<String, Object>> handleAppError(AppError error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(Map.of(
                        "code", error.getCode(),
                        "message", error.getMessage(),
                        "statusCode", error.getStatusCode()
                ));
    }
}
