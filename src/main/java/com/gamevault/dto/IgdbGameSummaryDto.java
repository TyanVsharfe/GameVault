package com.gamevault.dto;

import com.gamevault.dto.output.SeriesShort;
import com.gamevault.dto.output.igdb.*;

import java.util.List;

public record IgdbGameSummaryDto (
        long id,
        String name,
        Cover cover,
        GameType game_type,
        List<SeriesShort> collections,
        long first_release_date
){}
