package com.gamevault.http.igdb.interceptor;

import com.gamevault.component.IgdbTokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class IgdbAuthInterceptor implements Interceptor {

    private final IgdbTokenManager tokenManager;

    public IgdbAuthInterceptor(IgdbTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getAccessToken();

        if (token == null || token.trim().isEmpty()) {
            tokenManager.refreshToken();
        }
        Request original = chain.request();
        Request request = buildRequest(original);
        Response response = chain.proceed(request);

        if (response.code() == 401) {
            response.close();
            tokenManager.refreshToken();
            Request retryRequest = buildRequest(original);
            response = chain.proceed(retryRequest);
        }

        return response;
    }

    private Request buildRequest(Request original) {
        return original.newBuilder()
                .header("Client-ID", tokenManager.getClientId())
                .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                .build();
    }
}
