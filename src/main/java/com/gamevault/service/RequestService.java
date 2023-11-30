package com.gamevault.service;

import com.gamevault.data_template.API_CLIENT;
import com.google.gson.Gson;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    public static API_CLIENT getAPIKey() throws UnirestException {
        HttpResponse<JsonNode> jsonRequest = Unirest.post("https://id.twitch.tv/oauth2/token")
                .field("client_id","c5x4cwm5mp474yt174al5uwv11zuvx")
                .field("client_secret","zp3to8x9x28qihvjf0fh0ocx6deg7g")
                .field("grant_type","client_credentials")
                .asJson();
        return new Gson().fromJson(jsonRequest.getBody().toString(), API_CLIENT.class);
    }
}
