package com.gamevault.dto.output.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDTO(
        long id,
        String name,
        String summary,
        GameType game_type,
        List<GameMode> game_modes,
        long first_release_date,
        Company.Cover cover,
        List<IgdbGameDTO> dlcs,
        List<IgdbGameDTO> expansions,
        List<ReleaseDate> release_dates,
        List<Genre> genres,
        List<Platform> platforms,
        List<InvolvedCompany> involved_companies,
        List<Franchise> franchises,
        List<Series> collections
) {}

