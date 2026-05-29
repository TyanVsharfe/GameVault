package com.gamevault.dto.output.overview.series;

import com.gamevault.dto.output.overview.GameTypeGroupDto;

import java.util.List;

public record GameSeriesGroupsDto(
        List<GameTypeGroupDto> byGameType
) {}
