package com.gamevault.dto.output.enriched;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.igdb.*;

import java.util.List;

public record EnrichedGameSearchDto(
        Long id,
        String name,
        GameType gameType,
        Long firstReleaseDate,
        CoverDto cover,
        List<ReleaseDate> releaseDates,
        List<Platform> platforms,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        UserGameBatchData userData
) {
    public static EnrichedGameSearchDto fromIgdb(IgdbGameDto igdb) {
        return new EnrichedGameSearchDto(
                igdb.id(),
                igdb.name(),
                igdb.game_type(),
                igdb.first_release_date(),
                igdb.cover() != null ? new CoverDto(igdb.cover()) : null,
                igdb.release_dates(),
                igdb.platforms(),
                null
        );
    }

    public static EnrichedGameSearchDto fromIgdb(IgdbGameDto igdb, UserGameBatchData batchData) {
        return new EnrichedGameSearchDto(
                igdb.id(),
                igdb.name(),
                igdb.game_type(),
                igdb.first_release_date(),
                igdb.cover() != null ? new CoverDto(igdb.cover()) : null,
                igdb.release_dates(),
                igdb.platforms(),
                batchData
        );
    }
}
