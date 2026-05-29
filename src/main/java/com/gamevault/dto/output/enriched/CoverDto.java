package com.gamevault.dto.output.enriched;

import com.gamevault.dto.output.igdb.Cover;

public record CoverDto(
        String url,
        int imageId
) {
    public CoverDto(Cover cover) {
        this(cover.url(), cover.id());
    }
}
