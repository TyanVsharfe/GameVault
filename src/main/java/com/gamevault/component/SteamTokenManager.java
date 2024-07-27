package com.gamevault.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SteamTokenManager {
    @Value("${steam.api.key}")
    private String steam_api_key;

    public String getSteam_api_key() {
        return steam_api_key;
    }
}
