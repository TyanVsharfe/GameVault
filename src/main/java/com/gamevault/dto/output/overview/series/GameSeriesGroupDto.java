package com.gamevault.dto.output.overview.series;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.SeriesShort;
import com.gamevault.dto.output.overview.GamesStatsDto;

import java.util.List;

public record GameSeriesGroupDto(
        SeriesShort series,
        GamesStatsDto stats,
        List<IgdbGameSummaryDto> games
) {}
