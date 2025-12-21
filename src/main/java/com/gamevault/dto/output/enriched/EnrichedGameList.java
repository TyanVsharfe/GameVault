package com.gamevault.dto.output.enriched;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.model.UserGameListItem;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.igdb.GameType;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.dto.output.igdb.Platform;
import com.gamevault.dto.output.igdb.ReleaseDate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EnrichedGameList(
        UUID uuid,
        String name,
        String description,
        String authorUsername,
        List<EnrichedGameListItem> items,
        Instant createdAt,
        Instant updatedAt
) {
        public static EnrichedGameList fromUserGameList(UserGameList list) {
                return new EnrichedGameList(
                        list.getUuid(),
                        list.getName(),
                        list.getDescription(),
                        list.getAuthorUsername(),
                        list.getItems().stream().map(EnrichedGameListItem::fromUserGameListItem).toList(),
                        list.getCreatedAt(),
                        list.getUpdatedAt()
                );
        }

        public static EnrichedGameList fromUserGameList(UserGameList list, Map<Long, UserGameBatchData> batchData) {
                return new EnrichedGameList(
                        list.getUuid(),
                        list.getName(),
                        list.getDescription(),
                        list.getAuthorUsername(),
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
