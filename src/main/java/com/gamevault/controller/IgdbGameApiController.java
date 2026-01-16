package com.gamevault.controller;

import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.dto.output.igdb.Series;
import com.gamevault.service.IgdbGameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/igdb")
public class IgdbGameApiController {
    private final IgdbGameService igdbGameService;

    public IgdbGameApiController(IgdbGameService igdbGameService) {
        this.igdbGameService = igdbGameService;
    }

    @PostMapping("/games")
    public List<IgdbGameDto> gamesIGDB(@RequestBody String searchGame) {

        return igdbGameService.searchGames(searchGame);
    }

    @PostMapping("/games/ids")
    public List<IgdbGameDto> gamesIGDBids(@RequestBody Iterable<Long> igdbIds) {
        return igdbGameService.getGamesByIds(igdbIds);
    }

    @GetMapping("/games/{gameId}")
    public IgdbGameDto gameIGDB(@PathVariable Long gameId) {
        return igdbGameService.getGame(gameId);
    }

    @GetMapping("/series/{series}")
    public List<Series> gameSeries(@PathVariable String series) {
        return igdbGameService.getGameSeries(series);
    }

    @GetMapping("/game-company/{company}")
    public List<com.fasterxml.jackson.databind.JsonNode> getGameCompany(@PathVariable String company) {
        return igdbGameService.getGameCompany(company);
    }

    @GetMapping("/games/release-dates")
    public List<com.fasterxml.jackson.databind.JsonNode> gamesReleaseDates() {
        return igdbGameService.getGamesReleaseDates();
    }
}
