package com.gamevault.controller;

import com.gamevault.data_template.SteamGame;
import com.gamevault.service.SteamWebApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/steam")
public class SteamWebApiController {
    private final SteamWebApiService steamWebApiService;

    public SteamWebApiController(SteamWebApiService steamWebApiService) {
        this.steamWebApiService = steamWebApiService;
    }

    @GetMapping("/user/{id}/games")
    public String getGames(@PathVariable("id") Long steamId) {
        return steamWebApiService.getGames(steamId);
    }

    @GetMapping("/user/{id}/games/titles")
    public List<SteamGame> getGamesTitles(@PathVariable("id") Long steamId) {
        return steamWebApiService.getGamesTitles(steamId);
    }
}