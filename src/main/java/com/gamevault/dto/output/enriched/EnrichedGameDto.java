package com.gamevault.dto.output.enriched;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamevault.dto.output.igdb.*;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public record EnrichedGameDto(
        Long id,
        String name,
        String summary,
        GameType gameType,
        @JsonIgnore
        List<GameMode> gameModes,
        Long firstReleaseDate,
        CoverDto cover,
        @Nullable
        List<EnrichedGameDto> dlcs,
        @Nullable
        List<EnrichedGameDto> expansions,
        List<ReleaseDate> releaseDates,
        List<Genre> genres,
        List<Platform> platforms,
        List<InvolvedCompany> involvedCompanies,
        @Nullable
        List<Franchise> franchises,
        @Nullable
        List<Series> collections,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        UserGameData userData
) {
    public static EnrichedGameDto fromIgdb(IgdbGameDto igdb) {
        return new EnrichedGameDto(
                igdb.id(),
                igdb.name(),
                igdb.summary(),
                igdb.game_type(),
                igdb.game_modes(),
                igdb.first_release_date(),
                igdb.cover() != null ? new CoverDto(igdb.cover()) : null,
                convertDlcs(igdb.dlcs()),
                convertExpansions(igdb.expansions()),
                igdb.release_dates(),
                igdb.genres(),
                igdb.platforms(),
                igdb.involved_companies(),
                igdb.franchises(),
                igdb.collections(),
                null
        );
    }

    public static EnrichedGameDto fromIgdb(IgdbGameDto igdb, UserGameData userData) {
        return new EnrichedGameDto(
                igdb.id(),
                igdb.name(),
                igdb.summary(),
                igdb.game_type(),
                igdb.game_modes(),
                igdb.first_release_date(),
                igdb.cover() != null ? new CoverDto(igdb.cover()) : null,
                convertDlcs(igdb.dlcs(), userData != null ? userData.dlcs() : null),
                convertExpansions(igdb.expansions(), userData != null ? userData.expansions() : null),
                igdb.release_dates(),
                igdb.genres(),
                igdb.platforms(),
                igdb.involved_companies(),
                igdb.franchises(),
                igdb.collections(),
                userData
        );
    }

    private static List<EnrichedGameDto> convertDlcs(List<IgdbGameDto> dlcs) {
        return convertDlcs(dlcs, null);
    }

    private static List<EnrichedGameDto> convertDlcs(List<IgdbGameDto> dlcs, Map<Long, UserGameData> userDlcs) {
        if (dlcs == null) return null;
        return dlcs.stream()
                .map(dlc -> fromIgdb(dlc, userDlcs != null ? userDlcs.get(dlc.id()) : null))
                .toList();
    }

    private static List<EnrichedGameDto> convertExpansions(List<IgdbGameDto> expansions) {
        return convertExpansions(expansions, null);
    }

    private static List<EnrichedGameDto> convertExpansions(List<IgdbGameDto> expansions, Map<Long, UserGameData> userExpansions) {
        if (expansions == null) return null;
        return expansions.stream()
                .map(exp -> fromIgdb(exp, userExpansions != null ? userExpansions.get(exp.id()) : null))
                .toList();
    }
}

