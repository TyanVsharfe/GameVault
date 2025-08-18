package com.gamevault.dto.output;

import com.gamevault.enums.Enums;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class UserStatsDTO {
    private String username;
    private long totalGames;
    private Map<Enums.status, Long> gamesByStatus;
    private Double averageRating;
    private long totalNotes;
}