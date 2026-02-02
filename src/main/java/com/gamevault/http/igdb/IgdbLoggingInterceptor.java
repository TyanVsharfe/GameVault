package com.gamevault.http.igdb;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class IgdbLoggingInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        long start = System.nanoTime();

        Response response = chain.proceed(request);

        long tookMs =
                (System.nanoTime() - start) / 1_000_000;

        log.debug(
                "IGDB {} {} → {} ({} ms)",
                request.method(),
                request.url().encodedPath(),
                response.code(),
                tookMs
        );

        return response;
    }
}
