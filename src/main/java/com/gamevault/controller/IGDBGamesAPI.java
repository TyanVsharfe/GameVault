package com.gamevault.controller;

import com.gamevault.data_template.API_CLIENT;
import com.gamevault.service.RequestIGDBService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IGDBGamesAPI {
    @PostMapping("/games")
    public String gamesIGDB(@RequestBody String searchGame) throws UnirestException {
        API_CLIENT apiClient = RequestIGDBService.getAPIKey();

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", API_CLIENT.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, platforms, platforms.abbreviation, aggregated_rating, first_release_date, category;"
                        + "search *\"" + searchGame + "*\";"
                        + "where category = (0,8,9) & "
                        //+ "platforms = (0,8) & "
                        + "version_parent = null;"
                        + "limit 200;")
                .asJson();

        return jsonResponse.getBody().toString();
    }

    @PostMapping("/game/{gameId}")
    public String gameIGDB(@PathVariable String gameId) throws UnirestException {
        API_CLIENT apiClient = RequestIGDBService.getAPIKey();

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", API_CLIENT.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url, release_dates.y, status, " +
                        "category, summary, genres.name, first_release_date, platforms.abbreviation;"
                        + " where id = " + gameId + ";")
                .asJson();

        return jsonResponse.getBody().toString();
    }
}
