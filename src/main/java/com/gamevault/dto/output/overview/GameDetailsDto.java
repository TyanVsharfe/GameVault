package com.gamevault.dto.output.overview;

import com.gamevault.dto.IgdbGameSummaryDto;

import java.util.List;

public record GameDetailsDto<T, G>(
        T item,
        GamesStatsDto stats,
        List<IgdbGameSummaryDto> games,
        G groups
) {}
