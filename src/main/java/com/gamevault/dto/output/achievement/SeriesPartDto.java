package com.gamevault.dto.output.achievement;

import java.util.Set;

public record SeriesPartDto(
        long id,
        Set<AchievementGameDto> games,
        Boolean completed
) {}

