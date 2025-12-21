package com.gamevault.dto.output.enriched;

import com.gamevault.db.model.UserGameMode;
import com.gamevault.enums.Enums;

public record UserModeDto(
        Long id,
        Enums.GameModesIGDB modeName,
        Enums.Status status,
        Double rating
) {
    public static UserModeDto fromUserGameMode(UserGameMode mode) {
        if (mode == null) return null;
        return new UserModeDto(
                mode.getId(),
                mode.getMode(),
                mode.getStatus(),
                mode.getUserRating()
        );
    }
}
