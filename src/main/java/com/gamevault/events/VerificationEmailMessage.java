package com.gamevault.events;

import com.gamevault.enums.Enums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerificationEmailMessage(
        @NotNull @Size(max = 254) String email,
        @NotNull String token,
        @NotNull @Enumerated(EnumType.STRING) Enums.TokenType tokenType
) {}
