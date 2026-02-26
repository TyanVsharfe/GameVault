package com.gamevault.events;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerificationEmailEvent(
        @NotNull @Size(max = 254) String email,
        @NotNull String token
) {}
