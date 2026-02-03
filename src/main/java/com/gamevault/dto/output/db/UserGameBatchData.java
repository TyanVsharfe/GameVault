package com.gamevault.dto.output.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamevault.dto.output.enriched.GameListReference;
import com.gamevault.enums.Enums;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGameBatchData {
        private Long igdbId;
        private Long userGameId;
        private Enums.Status status;
        private Double userRating;
        private String review;
        private boolean isFullyCompleted;
        private boolean isOverallRating;
        private boolean isOverallStatus;
        private String userCoverUrl;
        private Instant createdAt;
        private Instant updatedAt;

        @Setter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<GameListReference> inLists;
}
