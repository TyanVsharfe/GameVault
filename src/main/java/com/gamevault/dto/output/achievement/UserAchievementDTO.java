package com.gamevault.dto.output.achievement;

public record UserAchievementDTO (
        Long id,
        AchievementDTO achievement,
        int currentProgress,
        boolean isCompleted
){}
