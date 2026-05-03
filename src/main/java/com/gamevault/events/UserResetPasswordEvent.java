package com.gamevault.events;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserResetPasswordEvent(
        @NotNull UUID userId,
        @NotNull @Size(max = 254) String email
) {}
