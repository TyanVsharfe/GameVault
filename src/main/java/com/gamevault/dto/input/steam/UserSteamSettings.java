package com.gamevault.dto.input.steam;

import org.springframework.lang.Nullable;

public record UserSteamSettings(
        @Nullable String steamId,
        @Nullable Boolean steamSyncEnabled
) {}
