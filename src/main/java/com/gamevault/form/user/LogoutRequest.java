package com.gamevault.form.user;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull String token,
        String refresh_token
) {}
