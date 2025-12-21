package com.gamevault.dto.output.db;

import com.gamevault.enums.Enums;

import java.time.Instant;

public record UserGameBatchData(
        Long userGameId,
        Long igdbId,
        Enums.Status status,
        Double userRating,
        String review,
        boolean isFullyCompleted,
        boolean isOverallRatingManual,
        boolean isOverallStatus,
        String userCoverUrl,
        Instant createdAt,
        Instant updatedAt,
        Long notesCount
) {}
