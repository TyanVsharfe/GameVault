package com.gamevault.config.interceptor;

import com.gamevault.component.IgdbTokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class IgdbRequestInterceptor implements Interceptor {

    private final IgdbTokenManager tokenManager;

    public IgdbRequestInterceptor(IgdbTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .addHeader("Client-ID", tokenManager.getClient_id())
                .addHeader(
                        "Authorization",
                        "Bearer " + tokenManager.getAccess_token()
                )
                .build();

        return chain.proceed(request);
    }
}
