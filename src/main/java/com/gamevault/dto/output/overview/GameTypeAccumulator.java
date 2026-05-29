package com.gamevault.dto.output.overview;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.igdb.GameType;

import java.util.ArrayList;
import java.util.List;

public record GameTypeAccumulator(
        GameType gameType,
        List<IgdbGameSummaryDto> games
) {
    public GameTypeAccumulator(GameType gameType) {
        this(gameType, new ArrayList<>());
    }
}
