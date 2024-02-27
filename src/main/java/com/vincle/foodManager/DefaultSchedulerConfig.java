package com.vincle.foodManager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scheduler")
@Getter
@Setter
public class DefaultSchedulerConfig {

    private int minRequests;
    private int maxRequests;

    private int maxRetry;

    // In milliseconds
    private int ttl;

    // In milliseconds
    private int interval;

    @Override
    public String toString() {
        return "DefaultSchedulerConfig{" +
                "minRequestsPerSecond=" + minRequests +
                ", maxRequestsPerSecond=" + maxRequests +
                ", maxRetry=" + maxRetry +
                ", ttl=" + ttl +
                ", interval=" + interval +
                '}';
    }
}
