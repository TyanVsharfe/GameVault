package com.gamevault.dto.input;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IgdbTokenResponse {
    private String access_token;
    private int expires_in;
    private String token_type;
}
