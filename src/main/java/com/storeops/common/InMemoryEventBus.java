package com.storeops.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryEventBus implements EventBus {
    private static final Logger log = LoggerFactory.getLogger(InMemoryEventBus.class);
    @Override
    public void publish(String topic, Object payload) {
        log.info("Event: {} payload={}", topic, payload);
    }
}
