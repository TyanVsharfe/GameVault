package com.gamevault.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint {
    private static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
    private static final String REALM_NAME = "GameVault authentication";

    private final ObjectMapper objectMapper;

    public BasicAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (isBadRequest(response)) {
            return;
        }
        response.setHeader(WWW_AUTHENTICATE_HEADER, "Basic realm \"%s\"".formatted(getRealmName()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String msg = authException.getMessage();
        String json = objectMapper.writeValueAsString(Map.of(
                "error", (msg == null || msg.isBlank()) ? "Authentication required" : msg
        ));
        response.getWriter().write(json);
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
