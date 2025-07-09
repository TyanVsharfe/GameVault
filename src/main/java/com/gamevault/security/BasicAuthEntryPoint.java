package com.gamevault.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.Writer;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint {
    private static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
    private static final String REALM_NAME = "GameVault authentication";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (isBadRequest(response)) {
            return;
        }
        response.setHeader(WWW_AUTHENTICATE_HEADER, "Basic realm \"%s\"".formatted(getRealmName()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Writer writer = response.getWriter();
        writer.append(authException.getMessage());
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName(REALM_NAME);
        super.afterPropertiesSet();
    }

    private boolean isBadRequest(HttpServletResponse response) {
        int statusCode = response.getStatus();
        return statusCode == HttpStatus.BAD_REQUEST.value() || statusCode >= HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
