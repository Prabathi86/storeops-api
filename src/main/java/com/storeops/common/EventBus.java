package com.storeops.common;

public interface EventBus {
    void publish(String topic, Object payload);
}
