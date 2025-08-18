package com.gamevault.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SteamTokenManager {
    @Value("${steam.api.key}")
    private String steam_api_key;
}
