package com.gamevault.dto.output.db;

import com.gamevault.enums.Enums;

import java.time.Instant;

public record UserGameBaseData(
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
        Long notesCount
) {}

