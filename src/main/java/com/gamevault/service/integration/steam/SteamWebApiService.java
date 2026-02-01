package com.gamevault.service.integration.steam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.component.SteamTokenManager;
import com.gamevault.dto.input.steam.SteamGame;
import com.gamevault.dto.input.steam.SteamResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Service
public class SteamWebApiService {
    private final SteamTokenManager steamTokenManager;

    public SteamWebApiService(SteamTokenManager steamTokenManager) {
        this.steamTokenManager = steamTokenManager;
    }

    public String getGames(@PathVariable("id") Long steamId) {
        try {
            HttpResponse<JsonNode> jsonRequest = Unirest.get("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001")
                    .queryString("key", steamTokenManager.getSteam_api_key())
                    .queryString("steamid", steamId)
                    .queryString("include_appinfo",1)
                    .asJson();

            if (jsonRequest.getStatus() == 500) {
                throw new Exception("Steam API Error");
            }

            return jsonRequest.getBody().toString();
        } catch (UnirestException | JSONException e) {
            throw new JSONException("Invalid Steam ID");
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching Steam games for user id {}: {}", steamId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public List<SteamGame> getGamesTitles(@PathVariable("id") Long steamId) {
        try {
            HttpResponse<JsonNode> jsonRequest = Unirest.get("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001")
                    .queryString("key", steamTokenManager.getSteam_api_key())
                    .queryString("steamid", steamId)
                    .queryString("include_appinfo",1)
                    .asJson();

            SteamResponse gameResponse = new ObjectMapper().readValue(jsonRequest.getBody().toString(), SteamResponse.class);

            return gameResponse.getResponse().getGames();
        } catch (UnirestException | JSONException e) {
            throw new JSONException("Invalid Steam ID");
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching Steam game titles for user id {}: {}", steamId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}
