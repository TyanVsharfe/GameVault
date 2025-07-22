package com.gamevault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.component.SteamTokenManager;
import com.gamevault.data_template.SteamGame;
import com.gamevault.data_template.SteamResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/steam")
public class SteamWebApiController {
    private final SteamTokenManager steamTokenManager;

    public SteamWebApiController(SteamTokenManager steamTokenManager) {
        this.steamTokenManager = steamTokenManager;
    }

    @GetMapping("/user/{id}/games")
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
            e.printStackTrace();
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/user/{id}/games/titles")
    public List<SteamGame> getGamesTitles(@PathVariable("id") Long steamId) {
        try {
            HttpResponse<JsonNode> jsonRequest = Unirest.get("https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001")
                    .queryString("key", steamTokenManager.getSteam_api_key())
                    .queryString("steamid", steamId)
                    .queryString("include_appinfo",1)
                    .asJson();

            SteamResponse gameResponse = new ObjectMapper().readValue(jsonRequest.getBody().toString(), SteamResponse.class);
            System.out.println("Steam games count " + gameResponse.getResponse().getGame_count());

            return gameResponse.getResponse().getGames();
        } catch (UnirestException | JSONException e) {
            throw new JSONException("Invalid Steam ID");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}