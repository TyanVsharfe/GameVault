package com.gamevault.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.data_template.IgdbTokenResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Getter
public class IgdbTokenManager {
    @Value("${igdb.client.id}")
    private String client_id;
    @Value("${igdb.client.secret}")
    private String client_secret;
    private String access_token;
    private Integer expires_in;
    private String token_type;

    // TODO Переписать автопродление access_token через refresh token
    @Scheduled(fixedRate = 4500000)
    public void getAPIKey() throws UnirestException, JsonProcessingException {
        HttpResponse<JsonNode> jsonRequest = Unirest.post("https://id.twitch.tv/oauth2/token")
                .field("client_id", client_id)
                .field("client_secret", client_secret)
                .field("grant_type","client_credentials")
                .asJson();
        IgdbTokenResponse igdbTokenResponse = new ObjectMapper().readValue(jsonRequest.getBody().toString(), IgdbTokenResponse.class);

        System.out.println("Access Token: " + igdbTokenResponse.getAccess_token());
        System.out.println("Expires In: " + igdbTokenResponse.getExpires_in());
        System.out.println("Token Type: " + igdbTokenResponse.getToken_type());

        this.access_token = igdbTokenResponse.getAccess_token();
        this.expires_in = igdbTokenResponse.getExpires_in();
        this.token_type = igdbTokenResponse.getToken_type();
    }
}
