package com.storeops.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppErrorTest {

    @Test
    void appErrorFields() {
        AppError error = new AppError("CODE", "Message", 400);
        assertEquals("CODE", error.getCode());
        assertEquals("Message", error.getMessage());
        assertEquals(400, error.getStatusCode());
    }
}
