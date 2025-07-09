package com.gamevault.controller;

import com.gamevault.data_template.SteamGameTitle;
import com.gamevault.service.IgdbGameService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/igdb")
public class IgdbGameAPI {
    private final IgdbGameService igdbGameService;

    public IgdbGameAPI(IgdbGameService igdbGameService) {
        this.igdbGameService = igdbGameService;
    }

    @PostMapping("/games")
    public String gamesIGDB(@RequestBody String searchGame) throws UnirestException {
        return igdbGameService.gamesIGDB(searchGame);
    }

    @PostMapping("/games/ids")
    public String gamesIGDBids(@RequestBody Iterable<Long> igdbIds) throws UnirestException {
        return igdbGameService.gamesIGDBids(igdbIds);
    }

    @GetMapping("/games/{gameId}")
    public String gameIGDB(@PathVariable String gameId) throws UnirestException {
        return igdbGameService.gameIGDB(gameId);
    }

    @GetMapping("/series/{seriesTitle}")
    public String gameSeries(@PathVariable String seriesTitle) throws UnirestException {
        return igdbGameService.gameSeries(seriesTitle);
    }

    @GetMapping("/games/release_dates")
    public String gamesReleaseDates() throws UnirestException {
        return igdbGameService.gamesReleaseDates();
    }

    @PostMapping("/steam-import")
    public String steamImportGamesIGDB(@RequestBody SteamGameTitle[] gameTitles) throws UnirestException {
        return igdbGameService.steamImportGamesIGDB(gameTitles);
    }
}
