package com.gamevault.domain;

public class ControllerNames {
    public static final String BASE_URL = "/api/v1";

    public static final String USERS_URI_PART = "/users";

    public static final String REGISTRATION_URI_PART = "/registration";
    public static final String LOGIN_URI_PART = "/login";
    public static final String REGISTRATION_URL = USERS_URI_PART + REGISTRATION_URI_PART;
    public static final String LOGIN_URL = USERS_URI_PART + LOGIN_URI_PART;
}
