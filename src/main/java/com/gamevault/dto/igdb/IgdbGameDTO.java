package com.gamevault.dto.igdb;

import java.util.List;

public record IgdbGameDTO(
        int id,
        String name,
        String summary,
        int game_type,
        long first_release_date,
        Company.Cover cover,
        List<ReleaseDate> release_dates,
        List<Genre> genres,
        List<Platform> platforms,
        List<InvolvedCompany> involved_companies,
        List<Franchise> franchises
) {}

