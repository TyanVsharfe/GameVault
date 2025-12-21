package com.gamevault.controller;

import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.dto.output.igdb.Series;
import com.gamevault.service.IgdbGameService;
import com.mashape.unirest.http.JsonNode;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/igdb")
public class IgdbGameAPI {
    private final IgdbGameService igdbGameService;

    public IgdbGameAPI(IgdbGameService igdbGameService) {
        this.igdbGameService = igdbGameService;
    }

    @PostMapping("/games")
    public Mono<List<IgdbGameDto>> gamesIGDB(@RequestBody String searchGame) {

        return igdbGameService.searchGames(searchGame);
    }

    @PostMapping("/games/ids")
    public Mono<List<IgdbGameDto>> gamesIGDBids(@RequestBody Iterable<Long> igdbIds) {
        return igdbGameService.getGamesByIds(igdbIds);
    }

    @GetMapping("/games/{gameId}")
    public Mono<IgdbGameDto> gameIGDB(@PathVariable Long gameId) {
        return igdbGameService.getGame(gameId);
    }

    @GetMapping("/series/{seriesTitle}")
    public Mono<List<Series>> gameSeries(@PathVariable String seriesTitle) {
        return igdbGameService.getGameSeries(seriesTitle);
    }

    @GetMapping("/games/release-dates")
    public Mono<List<com.fasterxml.jackson.databind.JsonNode>> gamesReleaseDates() {
        return igdbGameService.getGamesReleaseDates();
    }
}
