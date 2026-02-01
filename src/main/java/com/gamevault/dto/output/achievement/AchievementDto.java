package com.gamevault.dto.output.achievement;

import org.springframework.lang.Nullable;

import java.util.List;

public record AchievementDto(
        Long id,
        String name,
        String description,
        String category,
        int experiencePoints,
        int requiredCount,
        String iconUrl,
        @Nullable List<SeriesPartDto> seriesParts
){}
