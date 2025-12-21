package com.gamevault.dto.output.achievement;

public record UserAchievementDto(
        Long id,
        AchievementDto achievement,
        int currentProgress,
        boolean isCompleted
){}
