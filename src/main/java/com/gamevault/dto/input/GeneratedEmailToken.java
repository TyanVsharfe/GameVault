package com.gamevault.dto.input;

import com.gamevault.db.model.User;
import jakarta.validation.constraints.NotNull;

public record GeneratedEmailToken(
        @NotNull User user,
        @NotNull String rawToken
) {}
