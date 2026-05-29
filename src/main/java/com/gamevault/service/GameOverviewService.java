package com.gamevault.service;

import com.gamevault.dto.IgdbGameSummaryDto;
import com.gamevault.dto.output.SeriesShort;
import com.gamevault.dto.output.igdb.GameType;
import com.gamevault.dto.output.igdb.Series;
import com.gamevault.dto.output.overview.*;
import com.gamevault.dto.output.overview.company.CompanyShortDto;
import com.gamevault.dto.output.overview.company.GameCompanyGroupsDto;
import com.gamevault.dto.output.overview.company.GameCompanyOverviewDto;
import com.gamevault.dto.output.overview.series.GameSeriesAccumulator;
import com.gamevault.dto.output.overview.series.GameSeriesGroupDto;
import com.gamevault.dto.output.overview.series.GameSeriesGroupsDto;
import com.gamevault.service.integration.IgdbGameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameOverviewService {
    private final IgdbGameService igdbGameService;

    public GameOverviewService(IgdbGameService igdbGameService) {
        this.igdbGameService = igdbGameService;
    }

    public GameDetailsDto<CompanyShortDto, GameCompanyGroupsDto> getCompanyOverview(String companySlug) {
        GameCompanyOverviewDto company = igdbGameService.getGameCompany(companySlug)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Company not found: " + companySlug));

        List<IgdbGameSummaryDto> games = normalizeAndSortGames(company.developed());

        List<IgdbGameSummaryDto> withoutSeries = games.stream()
                .filter(this::hasNoSeries)
                .toList();

        List<GameSeriesGroupDto> bySeries = groupBySeries(games);
        List<GameTypeGroupDto> byGameType = groupByGameType(games);

        GamesStatsDto stats = buildStats(
                games,
                withoutSeries.size(),
                bySeries.size()
        );

        return new GameDetailsDto<>(
                toCompanyShort(company),
                stats,
                games,
                new GameCompanyGroupsDto(
                        bySeries,
                        byGameType,
                        withoutSeries
                )
        );
    }

    public GameDetailsDto<Series, GameSeriesGroupsDto> getSeriesOverview(String seriesSlug) {
        Series series = igdbGameService.getGameSeries(seriesSlug)
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Company not found: " + seriesSlug));

        List<IgdbGameSummaryDto> games = normalizeAndSortGames(series.games());

        List<IgdbGameSummaryDto> withoutSeries = games.stream()
                .filter(this::hasNoSeries)
                .toList();

        List<GameSeriesGroupDto> bySeries = groupBySeries(games);
        List<GameTypeGroupDto> byGameType = groupByGameType(games);

        GamesStatsDto stats = buildStats(
                games,
                withoutSeries.size(),
                bySeries.size()
        );

        return new GameDetailsDto<>(
                series,
                stats,
                games,
                new GameSeriesGroupsDto(
                        byGameType
                )
        );
    }

    private List<IgdbGameSummaryDto> normalizeAndSortGames(List<IgdbGameSummaryDto> games) {
        return Optional.ofNullable(games)
                .orElse(List.of())
                .stream()
                .sorted(
                        Comparator.comparingLong(this::safeReleaseDate)
                                .reversed()
                                .thenComparing(IgdbGameSummaryDto::name, Comparator.nullsLast(String::compareToIgnoreCase))
                )
                .toList();
    }

    private List<GameSeriesGroupDto> groupBySeries(List<IgdbGameSummaryDto> games) {
        Map<Integer, GameSeriesAccumulator> grouped = new LinkedHashMap<>();

        for (IgdbGameSummaryDto game : games) {
            if (hasNoSeries(game)) {
                continue;
            }

            for (SeriesShort series : game.collections()) {
                grouped.computeIfAbsent(
                        series.id(),
                        id -> new GameSeriesAccumulator(series)
                ).games().add(game);
            }
        }

        return grouped.values()
                .stream()
                .map(acc -> {
                    List<IgdbGameSummaryDto> groupGames = acc.games();

                    return new GameSeriesGroupDto(
                            acc.series(),
                            buildStats(groupGames, 0, 0),
                            groupGames
                    );
                })
                .sorted(
                        Comparator.comparingInt((GameSeriesGroupDto group) -> group.stats().total())
                                .reversed()
                                .thenComparing(group -> group.series().name(), String.CASE_INSENSITIVE_ORDER)
                )
                .toList();
    }

    private List<GameTypeGroupDto> groupByGameType(List<IgdbGameSummaryDto> games) {
        Map<Integer, GameTypeAccumulator> grouped = new LinkedHashMap<>();

        for (IgdbGameSummaryDto game : games) {
            GameType gameType = game.game_type();

            if (gameType == null) {
                continue;
            }

            grouped.computeIfAbsent(
                    gameType.id(),
                    id -> new GameTypeAccumulator(gameType)
            ).games().add(game);
        }

        return grouped.values()
                .stream()
                .map(acc -> {
                    List<IgdbGameSummaryDto> groupGames = acc.games();

                    return new GameTypeGroupDto(
                            acc.gameType(),
                            buildStats(groupGames, 0, 0),
                            groupGames
                    );
                })
                .sorted(
                        Comparator.comparingInt((GameTypeGroupDto group) -> group.stats().total())
                                .reversed()
                                .thenComparing(group -> group.gameType().type(), String.CASE_INSENSITIVE_ORDER)
                )
                .toList();
    }

    private GamesStatsDto buildStats(List<IgdbGameSummaryDto> games, int withoutSeries, int seriesCount) {
        return new GamesStatsDto(
                games.size(),
                countByType(games, 0),
                countByType(games, 1),
                countByType(games, 2),
                countByType(games, 4),
                countByType(games, 3),
                countByType(games, 8),
                countByType(games, 9),
                countByType(games, 14),
                withoutSeries,
                seriesCount
        );
    }

    private int countByType(List<IgdbGameSummaryDto> games, int typeId) {
        return (int) games.stream()
                .filter(game -> game.game_type() != null)
                .filter(game -> game.game_type().id() == typeId)
                .count();
    }

    private boolean hasNoSeries(IgdbGameSummaryDto game) {
        return game.collections() == null || game.collections().isEmpty();
    }

    private long safeReleaseDate(IgdbGameSummaryDto game) {
        return game.first_release_date() > 0
                ? game.first_release_date()
                : Long.MIN_VALUE;
    }

    private CompanyShortDto toCompanyShort(GameCompanyOverviewDto company) {
        return new CompanyShortDto(
                company.id(),
                company.name(),
                company.slug(),
                company.description(),
                company.logo(),
                company.country(),
                company.first_release_date(),
                company.url()
        );
    }
}
