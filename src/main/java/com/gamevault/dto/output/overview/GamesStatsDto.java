package com.gamevault.dto.output.overview;

public record GamesStatsDto(
        int total,
        int mainGames,
        int dlc,
        int expansions,
        int standaloneExpansions,
        int bundles,
        int remakes,
        int remasters,
        int updates,
        int withoutSeries,
        int SeriesCount
) {}
