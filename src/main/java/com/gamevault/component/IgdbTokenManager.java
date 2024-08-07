package com.gamevault.component;

import com.gamevault.data_template.IgdbTokenResponse;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IgdbTokenManager {
    @Value("${client.id}")
    private String client_id;
    @Value("${client.secret}")
    private String client_secret;
    private String access_token;
    private Integer expires_in;
    private String token_type;

    // TODO Переписать автопродление access_token через refresh token
    @Scheduled(fixedRate = 4500000)
    public void getAPIKey() throws UnirestException {
        HttpResponse<JsonNode> jsonRequest = Unirest.post("https://id.twitch.tv/oauth2/token")
                .field("client_id", client_id)
                .field("client_secret", client_secret)
                .field("grant_type","client_credentials")
                .asJson();
        IgdbTokenResponse igdbTokenResponse = new Gson().fromJson(jsonRequest.getBody().toString(), IgdbTokenResponse.class);

        System.out.println("Access Token: " + igdbTokenResponse.getAccess_token());
        System.out.println("Expires In: " + igdbTokenResponse.getExpires_in());
        System.out.println("Token Type: " + igdbTokenResponse.getToken_type());

        this.access_token = igdbTokenResponse.getAccess_token();
        this.expires_in = igdbTokenResponse.getExpires_in();
        this.token_type = igdbTokenResponse.getToken_type();
    }

    public String getAccess_token() {
        return access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }
}
