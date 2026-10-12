package com.storeops.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EventBusTest {

    @Test
    void publishDoesNotThrow() {
        InMemoryEventBus bus = new InMemoryEventBus();
        assertDoesNotThrow(() -> bus.publish("test.topic", "payload"));
    }
}
