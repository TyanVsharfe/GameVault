package com.gamevault.dto.output.igdb;

import java.util.List;
public record Series(
        int id,
        String name,
        String slug,
        List<IgdbGameDTO> games
) {}
