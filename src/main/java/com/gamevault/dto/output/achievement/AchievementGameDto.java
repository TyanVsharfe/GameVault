package com.gamevault.dto.output.achievement;

public record AchievementGameDto(
        long id,
        String name,
        String coverUrl,
        Integer releaseYear
) {}
