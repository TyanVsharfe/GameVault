package com.gamevault.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gamevault.dto.output.overview.company.CompanyShortDto;
import com.gamevault.dto.output.overview.company.GameCompanyGroupsDto;
import com.gamevault.dto.output.overview.series.GameSeriesGroupsDto;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.dto.output.igdb.Series;
import com.gamevault.dto.output.overview.GameDetailsDto;
import com.gamevault.service.GameOverviewService;
import com.gamevault.service.integration.IgdbGameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/igdb")
public class IgdbGameApiController {
    private final IgdbGameService igdbGameService;
    private final GameOverviewService gameOverviewService;

    public IgdbGameApiController(IgdbGameService igdbGameService, GameOverviewService gameOverviewService) {
        this.igdbGameService = igdbGameService;
        this.gameOverviewService = gameOverviewService;
    }

    @PostMapping("/games")
    public List<IgdbGameDto> gamesIGDB(@RequestBody String searchGame) {

        return igdbGameService.searchGames(searchGame);
    }

    @PostMapping("/games/ids")
    public List<JsonNode> gamesIGDBids(@RequestBody List<Long> igdbIds) {
        return igdbGameService.getGamesByIds(igdbIds);
    }

    @GetMapping("/games/{gameId}")
    public IgdbGameDto gameIGDB(@PathVariable Long gameId) {
        return igdbGameService.getGame(gameId);
    }

    @GetMapping("/series/{series}")
    public GameDetailsDto<Series, GameSeriesGroupsDto> gameSeries(@PathVariable String series) {
        return gameOverviewService.getSeriesOverview(series);
    }

    @GetMapping("/game-company/{company}")
    public GameDetailsDto<CompanyShortDto, GameCompanyGroupsDto> getGameCompany(@PathVariable String company) {
        return gameOverviewService.getCompanyOverview(company);
    }

    @GetMapping("/games/release-dates")
    public List<JsonNode> gamesReleaseDates() {
        return igdbGameService.getGamesReleaseDates();
    }
}
