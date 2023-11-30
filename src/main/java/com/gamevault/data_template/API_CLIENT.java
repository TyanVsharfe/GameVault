package com.gamevault.data_template;

public class API_CLIENT {
    private static final String client_id = "c5x4cwm5mp474yt174al5uwv11zuvx";
    String access_token;
    Integer expires_in;
    String token_type = "bearer";

    public String getAccess_token() {
        return access_token;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    public static String getClient_id() {
        return client_id;
    }
}
