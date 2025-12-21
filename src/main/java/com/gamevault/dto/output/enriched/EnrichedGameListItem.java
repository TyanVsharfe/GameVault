package com.gamevault.dto.output.enriched;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamevault.db.model.UserGameListItem;
import com.gamevault.dto.output.db.UserGameBatchData;

import java.util.UUID;

public record EnrichedGameListItem(
        UUID uuid,
        Integer order,
        GameDto game,
        String note,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        UserGameBatchData userData
) {
    public static EnrichedGameListItem fromUserGameListItem(UserGameListItem listItem) {
        return new EnrichedGameListItem(
                listItem.getUuid(),
                listItem.getOrder(),
                GameDto.fromEntity(listItem.getGame()),
                listItem.getNote(),
                null
        );
    }

    public static EnrichedGameListItem fromUserGameListItem(UserGameListItem listItem, UserGameBatchData batchData) {
        return new EnrichedGameListItem(
                listItem.getUuid(),
                listItem.getOrder(),
                GameDto.fromEntity(listItem.getGame()),
                listItem.getNote(),
                batchData
        );
    }
}