package com.gamevault.dto.output.enriched;

import com.gamevault.dto.output.igdb.Company;

public record CoverDto(
        String url,
        int imageId
) {
    public CoverDto(Company.Cover cover) {
        this(cover.url(), cover.id());
    }
}
