package com.gamevault.domain;

import com.gamevault.component.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControllerNames {

    private final ApiProperties apiProperties;

    private static final String USERS_URI_PART = "/users";
    private static final String REGISTRATION_URI_PART = "/registration";
    private static final String LOGIN_URI_PART = "/login";

    public String getApiPrefix() {
        return apiProperties.getPrefix();
    }

    public String getRegistrationUrl() {
        return getApiPrefix() + USERS_URI_PART + REGISTRATION_URI_PART;
    }

    public String getLoginUrl() {
        return getApiPrefix() + USERS_URI_PART + LOGIN_URI_PART;
    }

    public String getUserReviewsUrl() {
        return getApiPrefix() + USERS_URI_PART + "/games/*/reviews";
    }

    public String getIgdbPattern() {
        return getApiPrefix() + "/igdb/**";
    }
}
