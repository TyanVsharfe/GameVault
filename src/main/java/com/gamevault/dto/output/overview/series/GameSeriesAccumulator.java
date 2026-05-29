package com.gamevault.dto.output.overview.series;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.SeriesShort;

import java.util.ArrayList;
import java.util.List;

public record GameSeriesAccumulator(
        SeriesShort series,
        List<IgdbGameSummaryDto> games
) {
    public GameSeriesAccumulator(SeriesShort collection) {
        this(collection, new ArrayList<>());
    }
}
