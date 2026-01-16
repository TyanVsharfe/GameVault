package com.gamevault.dto.output.enriched;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.model.UserGameMode;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.enums.Enums;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record UserGameData(
        Long userGameId,
        Enums.Status status,
        Double userRating,
        String review,
        boolean isFullyCompleted,
        boolean isOverallRating,
        boolean isOverallStatus,
        String userCoverUrl,
        Instant createdAt,
        Instant updatedAt,
        Long notesCount,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<UserModeDto> userModes,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<Long, UserGameData> dlcs,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<Long, UserGameData> expansions,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<GameListReference> inLists
) {
    public static UserGameData fromUserGame(UserGame userGame) {
        if (userGame == null) {
            return null;
        }
        List<UserModeDto> userModes;
        if (userGame.isOverallRating() && userGame.isOverallStatus()) {
            userModes = null;
        }
        else {
            userModes = convertUserModes(userGame.getUserModes());
        }

        return new UserGameData(
                userGame.getId(),
                userGame.getStatus(),
                userGame.getUserRating(),
                userGame.getReview(),
                userGame.isFullyCompleted(),
                userGame.isOverallRating(),
                userGame.isOverallStatus(),
                userGame.getUserCoverUrl(),
                userGame.getCreatedAt(),
                userGame.getUpdatedAt(),
                (long) (userGame.getNotes() != null ? userGame.getNotes().size() : 0),
                userModes,
                convertDlcsToMap(userGame.getDlcs()),
                null,
                null
        );
    }

    public static UserGameData fromUserGame(UserGame userGame, List<GameListReference> lists) {
        if (userGame == null) return null;

        UserGameData base = fromUserGame(userGame);
        return new UserGameData(
                base.userGameId(),
                base.status(),
                base.userRating(),
                base.review(),
                base.isFullyCompleted(),
                base.isOverallRating(),
                base.isOverallStatus(),
                base.userCoverUrl(),
                base.createdAt(),
                base.updatedAt(),
                base.notesCount(),
                base.userModes(),
                base.dlcs(),
                base.expansions(),
                lists
        );
    }

    private static List<UserModeDto> convertUserModes(List<UserGameMode> modes) {
        if (modes == null) return List.of();
        return modes.stream()
                .map(UserModeDto::fromUserGameMode)
                .toList();
    }

    private static Map<Long, UserGameData> convertDlcsToMap(List<UserGame> dlcs) {
        if (dlcs == null || dlcs.isEmpty()) return null;
        return dlcs.stream()
                .collect(Collectors.toMap(
                        dlc -> dlc.getGame().getIgdbId(),
                        UserGameData::fromUserGame
                ));
    }

    public static UserGameData fromUserGameBase(UserGameBaseData base, List<UserModeDto> modes) {
        if (base == null) {
            return null;
        }
        List<UserModeDto> userModes;
        if (base.isOverallRating() && base.isOverallStatus()) {
            userModes = null;
        }
        else {
            userModes = modes;
        }

        return new UserGameData(
                base.userGameId(),
                base.status(),
                base.userRating(),
                base.review(),
                base.isFullyCompleted(),
                base.isOverallRating(),
                base.isOverallStatus(),
                base.userCoverUrl(),
                base.createdAt(),
                base.updatedAt(),
                base.notesCount(),
                userModes,
                null,
                null,
                null
        );
    }
}
