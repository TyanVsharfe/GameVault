package com.gamevault.dto.input.steam;

import com.gamevault.enums.Enums;
import org.springframework.lang.Nullable;

import java.util.Set;

public record UserSteamSync(
        @Nullable Boolean steamSyncEnabled,
        @Nullable Set<Long> ignoredGameIds,
        @Nullable Enums.SteamSync syncFrequency
) {}
