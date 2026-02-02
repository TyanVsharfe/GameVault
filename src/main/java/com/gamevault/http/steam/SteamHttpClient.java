package com.gamevault.http.steam;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SteamHttpClient {
    private static final String BASE_URL = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001";

    private final OkHttpClient client;

    public SteamHttpClient(@Qualifier("steamOkHttpClient") OkHttpClient client) {
        this.client = client;
    }

    public String getOwnedGames(long steamId) {
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("steamid", String.valueOf(steamId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new IllegalArgumentException("Steam connection error", e);
        }
    }
}
