package com.gamevault.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetrics {

    private final MeterRegistry registry;

    public CustomMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementUserAction(String action) {
        Counter.builder("user_actions_total")
                .tag("action", action)
                .register(registry)
                .increment();
    }

    public void incrementUserAuth(String action) {
        Counter.builder("user_auth_total")
                .tag("type", action)
                .register(registry)
                .increment();
    }
}