package com.gamevault.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.dto.input.IgdbTokenResponse;
import com.gamevault.exception.IgdbApiException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Component
@Slf4j
public class IgdbTokenManager {
    @Getter
    @Value("${igdb.client.id}")
    private String clientId;
    @Value("${igdb.client.secret}")
    private String clientSecret;
    private String accessToken;
    private String tokenType;

    private final OkHttpClient tokenClient;

    private final AtomicReference<CompletableFuture<Void>> pendingRefresh = new AtomicReference<>(null);

    public IgdbTokenManager(@Qualifier("baseOkHttpClient") OkHttpClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing IGDB token on startup...");
        try {
            refreshToken();
        } catch (Exception e) {
            log.error("Failed to initialize IGDB token", e);
        }
    }

    public void getApiKey() {
        log.info("Refreshing IGDB access token...");

        RequestBody body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url("https://id.twitch.tv/oauth2/token")
                .post(body)
                .build();

        try (Response response = tokenClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Token refresh returned HTTP " + response.code());
            }

            if (response.body() == null) {
                throw new IgdbApiException("IGDB API response body is empty");
            }

            IgdbTokenResponse tokenResponse = new ObjectMapper()
                    .readValue(response.body().string(), IgdbTokenResponse.class);

            this.accessToken = tokenResponse.getAccess_token();
            this.tokenType = tokenResponse.getToken_type();

            log.info("Access token refreshed. Expires in: {} sec", tokenResponse.getExpires_in());

        } catch (IOException e) {
            throw new RuntimeException("Failed to refresh token", e);
        }
    }

    public void refreshToken() {
        CompletableFuture<Void> myFuture = new CompletableFuture<>();

        boolean isOwner = pendingRefresh.compareAndSet(null, myFuture);

        CompletableFuture<Void> activeFuture = isOwner
                ? myFuture
                : pendingRefresh.get();

        if (isOwner) {
            try {
                getApiKey();
                myFuture.complete(null);
            } catch (Exception e) {
                myFuture.completeExceptionally(e);
            } finally {
                pendingRefresh.compareAndSet(myFuture, null);
            }
        }

        try {
            activeFuture.join();
        } catch (CompletionException e) {
            throw new RuntimeException("Token refresh failed", e.getCause());
        }
    }
}
