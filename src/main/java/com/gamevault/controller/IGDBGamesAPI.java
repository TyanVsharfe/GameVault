package com.gamevault.controller;

import com.gamevault.data_template.API_CLIENT;
import com.gamevault.data_template.Enums;
import com.gamevault.service.RequestService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IGDBGamesAPI {
    // TODO Переместить этот метод в другой контроллер, связанный именно с этим API (Еще придумать пути с сайтами) !!СДЕЛАНО!!
    @PostMapping("/games")
    public String gamesIGDB(@RequestBody String searchGame) throws UnirestException {
        API_CLIENT apiClient = RequestService.getAPIKey();

        HttpResponse<JsonNode> jsonResponse = Unirest.post("https://api.igdb.com/v4/games")
                .header("Client-ID", API_CLIENT.getClient_id())
                .header("Authorization", "Bearer " + apiClient.getAccess_token())
                .body("fields name,cover.url,category; search \""
                        + searchGame + "\";"
                        + "where category = (0,8) & "
                        + "version_parent = null;"
                        //+ "sort asc;"
                        + "limit 200;")
                .asJson();

        return jsonResponse.getBody().toString();
    }
}
