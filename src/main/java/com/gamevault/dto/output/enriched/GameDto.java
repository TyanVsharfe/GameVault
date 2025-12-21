package com.gamevault.dto.output.enriched;

import com.gamevault.db.model.Game;
import com.gamevault.enums.Enums;

public record GameDto(
        Long igdbId,
        String title,
        String coverUrl,
        Enums.CategoryIGDB category
) {
    public static GameDto fromEntity(Game game) {
        return new GameDto(
                game.getIgdbId(),
                game.getTitle(),
                game.getCoverUrl(),
                game.getCategory()
        );
    }
}
