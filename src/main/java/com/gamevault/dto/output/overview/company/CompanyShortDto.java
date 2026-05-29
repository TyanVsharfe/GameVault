package com.gamevault.dto.output.overview.company;

import com.gamevault.dto.output.igdb.Cover;

public record CompanyShortDto(
        Long id,
        String name,
        String slug,
        String description,
        Cover logo,
        Integer country,
        Long first_release_date,
        String url
) {}

