package com.gamevault.service.integration.steam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.dto.input.steam.SteamGame;
import com.gamevault.dto.input.steam.SteamResponse;
import com.gamevault.http.steam.SteamHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Service
public class SteamWebApiService {
    private final SteamHttpClient steamHttpClient;

    public SteamWebApiService(SteamHttpClient steamHttpClient) {
        this.steamHttpClient = steamHttpClient;
    }

    public String getGames(@PathVariable("id") Long steamId) {
        return steamHttpClient.getOwnedGames(steamId);
    }

    public List<SteamGame> getGamesTitles(@PathVariable("id") Long steamId) {
        try {
            String games = steamHttpClient.getOwnedGames(steamId);

            SteamResponse gameResponse = new ObjectMapper().readValue(games, SteamResponse.class);

            return gameResponse.getResponse().getGames();
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching Steam game titles for user id {}: {}", steamId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}
