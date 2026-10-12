package com.storeops.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppErrorTest {

    @Test
    void appError_fields() {
        AppError err = new AppError("TEST", "Something happened", 400);

        assertEquals("TEST", err.getCode());
        assertEquals("Something happened", err.getMessage());
        assertEquals(400, err.getStatusCode());
    }

    @Test
    void notFoundError() {
        NotFoundError err = new NotFoundError("NOT_FOUND", "Missing");

        assertEquals("NOT_FOUND", err.getCode());
        assertEquals("Missing", err.getMessage());
        assertEquals(404, err.getStatusCode());
    }

    @Test
    void validationError() {
        ValidationError err = new ValidationError("BAD", "Invalid");

        assertEquals("BAD", err.getCode());
        assertEquals("Invalid", err.getMessage());
        assertEquals(400, err.getStatusCode());
    }
}
