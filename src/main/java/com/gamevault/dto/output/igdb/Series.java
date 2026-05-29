package com.gamevault.dto.output.igdb;

import com.gamevault.dto.IgdbGameSummaryDto;
import java.util.List;

public record Series(
        int id,
        String name,
        String slug,
        List<IgdbGameSummaryDto> games
) {}
