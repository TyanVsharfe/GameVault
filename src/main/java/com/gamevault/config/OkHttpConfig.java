package com.gamevault.config;

import com.gamevault.component.IgdbTokenManager;
import com.gamevault.component.SteamTokenManager;
import com.gamevault.http.igdb.IgdbLoggingInterceptor;
import com.gamevault.http.igdb.interceptor.IgdbAuthInterceptor;
import com.gamevault.http.steam.interceptor.SteamRequestInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {
    @Bean
    @Qualifier("baseOkHttpClient")
    public OkHttpClient baseOkHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    @Bean
    @Qualifier("igdbOkHttpClient")
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
                .addInterceptor(new IgdbAuthInterceptor(tokenManager))
                .addInterceptor(new IgdbLoggingInterceptor())
                .build();
    }

    @Bean
    @Qualifier("steamOkHttpClient")
    public OkHttpClient steamOkHttpClient(SteamTokenManager tokenManager) {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(
                        10,
                        10, TimeUnit.MINUTES
                ))
                .addInterceptor(new SteamRequestInterceptor(tokenManager))
                .build();
    }
}
