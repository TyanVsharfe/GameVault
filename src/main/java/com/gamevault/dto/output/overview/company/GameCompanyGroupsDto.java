package com.gamevault.dto.output.overview.company;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.overview.GameTypeGroupDto;
import com.gamevault.dto.output.overview.series.GameSeriesGroupDto;

import java.util.List;

public record GameCompanyGroupsDto(
        List<GameSeriesGroupDto> byCollection,
        List<GameTypeGroupDto> byGameType,
        List<IgdbGameSummaryDto> withoutSeries
) {}
