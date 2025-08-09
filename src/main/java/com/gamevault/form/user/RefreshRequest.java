package com.gamevault.form.user;

import jakarta.validation.constraints.NotNull;

public record RefreshRequest(
        @NotNull String refresh_token
) {}
