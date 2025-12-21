package com.gamevault.dto.output.achievement;

public record AchievementDto(
        Long id,
        String name,
        String description,
        String category,
        int experiencePoints,
        int requiredCount,
        String iconUrl
){}
