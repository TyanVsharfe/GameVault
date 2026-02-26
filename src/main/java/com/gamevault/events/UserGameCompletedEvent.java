package com.gamevault.events;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import jakarta.validation.constraints.NotNull;

public record UserGameCompletedEvent(
        @NotNull User user,
        @NotNull UserGame userGame
) {}


