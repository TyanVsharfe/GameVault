package com.gamevault.dto.input;

import java.util.List;
import java.util.UUID;

public record SteamImportTask(
        UUID userId,
        Long steamId,
        List<Long> gameIds
) {}
