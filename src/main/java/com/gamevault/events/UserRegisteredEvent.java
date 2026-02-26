package com.gamevault.events;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRegisteredEvent(
        @NotNull UUID userId
) {}
