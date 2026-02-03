package com.gamevault.dto.output.enriched;

import java.util.UUID;

public record GameListReference(
        UUID listId,
        String listName,
        Boolean isPublic
) {}
