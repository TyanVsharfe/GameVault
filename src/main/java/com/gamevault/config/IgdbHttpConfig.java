package com.gamevault.config;

import com.gamevault.component.IgdbTokenManager;
import com.gamevault.config.interceptor.IgdbLoggingInterceptor;
import com.gamevault.config.interceptor.IgdbRequestInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class IgdbHttpConfig {

    @Bean
    public OkHttpClient igdbOkHttpClient(IgdbTokenManager tokenManager) {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15))
                .writeTimeout(Duration.ofSeconds(10))
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(
                        10,
                        10, TimeUnit.MINUTES
                ))
                .addInterceptor(new IgdbRequestInterceptor(tokenManager))
                .addInterceptor(new IgdbLoggingInterceptor())
                .build();
    }
}
