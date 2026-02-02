package com.gamevault.http.steam.interceptor;

import com.gamevault.component.SteamTokenManager;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.rmi.ServerException;

public class SteamRequestInterceptor implements Interceptor {

    private final SteamTokenManager tokenManager;

    public SteamRequestInterceptor(SteamTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalUrl = original.url();

        HttpUrl newUrl = originalUrl.newBuilder()
                .addQueryParameter("key", tokenManager.getSteamApiKey())
                .addQueryParameter("include_appinfo", "1")
                .build();

        Request request = original.newBuilder()
                .url(newUrl)
                .build();

        Response response = chain.proceed(request);

        if (!response.isSuccessful()) {
            throw new ServerException("Steam API error: " + response.code());
        }

        return response;
    }
}
