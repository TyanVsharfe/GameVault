package com.gamevault.dto.output.achievement;

public record AchievementDTO (
        Long id,
        String name,
        String description,
        String category,
        int experiencePoints,
        int requiredCount,
        String iconUrl

){}
