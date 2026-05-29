package com.gamevault.dto.output.overview.company;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.igdb.Cover;

import java.util.List;

public record GameCompanyOverviewDto(
        Long id, String name,
        String slug,
        String description,
        Cover logo,
        Integer country,
        Long first_release_date,
        String url,
        List<IgdbGameSummaryDto> developed
) {}
