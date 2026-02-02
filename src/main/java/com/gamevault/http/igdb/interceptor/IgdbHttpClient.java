package com.gamevault.http.igdb.interceptor;

import com.gamevault.exception.IgdbApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class IgdbHttpClient {

    private static final String BASE_URL = "https://api.igdb.com/v4";
    private static final MediaType TEXT = MediaType.get("text/plain; charset=utf-8");
    private final OkHttpClient client;

    public IgdbHttpClient(@Qualifier("igdbOkHttpClient") OkHttpClient client) {
        this.client = client;
    }

    public String post(String endpoint, String body) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL))
                .newBuilder()
                .addPathSegment(endpoint)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, TEXT))
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                logIgdbApiError(response, response.code());
            }

            ResponseBody responseBody = response.body();

            if (responseBody == null) {
                throw new IgdbApiException("IGDB API response body is empty");
            }

            return responseBody.string();
        } catch (IOException e) {
            throw new IgdbApiException("Failed to call IGDB API: " + e.getMessage());
        }
    }

    private void logIgdbApiError(Response response, int code) throws IOException {
        String error = response.body() != null
                ? response.body().string()
                : "<empty>";

        log.error("IGDB API error: status={}, body={}", code, error);

        throw new IgdbApiException(
                "IGDB API returned error: " + code
        );
    }
}
