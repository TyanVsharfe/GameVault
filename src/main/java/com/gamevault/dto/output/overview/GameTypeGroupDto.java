package com.gamevault.dto.output.overview;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.igdb.GameType;

import java.util.List;

public record GameTypeGroupDto(
        GameType gameType,
        GamesStatsDto stats,
        List<IgdbGameSummaryDto> games
) {}
