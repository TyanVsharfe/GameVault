package com.gamevault.events;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;

public record UserGameCompletedEvent(
        User user,
        UserGame userGame
) {}


