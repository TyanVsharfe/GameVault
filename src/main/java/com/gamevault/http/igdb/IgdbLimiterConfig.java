package com.gamevault.http.igdb;

import org.springframework.context.annotation.Configuration;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@Configuration
public class IgdbLimiterConfig {
    @Bean
    public Bucket igdbBucket() {
        return Bucket.builder()
                .addLimit(limit ->
                        limit.capacity(12).refillGreedy(8, Duration.ofSeconds(1)))
                .build();
    }
}
