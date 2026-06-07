package com.gamevault.dto.output.enriched;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.model.UserGameListItem;
import com.gamevault.dto.output.db.UserGameBatchData;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EnrichedGameList(
        UUID uuid,
        String name,
        String description,
        String authorUsername,
        Boolean isPublic,
        Boolean isOwned,
        List<EnrichedGameListItem> items,
        Instant createdAt,
        Instant updatedAt
) {
    public static EnrichedGameList fromUserGameList(UserGameList list, User currentUser) {
        return new EnrichedGameList(
                list.getUuid(),
                list.getName(),
                list.getDescription(),
                list.getAuthorUsername(),
                list.isPublic(),
                currentUser != null && list.isOwnedBy(currentUser),
                list.getItems().stream().map(EnrichedGameListItem::fromUserGameListItem).toList(),
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    public static EnrichedGameList fromUserGameList(
            UserGameList list,
            Map<Long, UserGameBatchData> batchData,
            User currentUser) {
        return new EnrichedGameList(
                list.getUuid(),
                list.getName(),
                list.getDescription(),
                list.getAuthorUsername(),
                list.isPublic(),
                list.isOwnedBy(currentUser),
                list.getItems().stream()
                        .map(item -> createEnrichedItem(item, batchData))
                        .toList(),
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    private static EnrichedGameListItem createEnrichedItem(UserGameListItem item, Map<Long, UserGameBatchData> batchData) {
        UserGameBatchData userData = null;
        if (item.getGame() != null && item.getGame().getIgdbId() != null) {
            userData = batchData.get(item.getGame().getIgdbId());
        }
        return EnrichedGameListItem.fromUserGameListItem(item, userData);
    }
}
