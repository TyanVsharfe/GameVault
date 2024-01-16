package com.gamevault.controller;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Game;
import com.gamevault.form.GameForm;
import com.gamevault.form.GameUpdateDTO;
import com.gamevault.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RESTController {
    final GameService gameService;

    public RESTController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/game/{id}")
    public Optional<Game> get(@PathVariable("id") Long id) {
        return gameService.getGame(id);
    }

    @GetMapping("/game/igdb/{IgdbId}")
    public Optional<Game> getByIgdbId(@PathVariable("IgdbId") Long igdbId) {
        return gameService.getGameByIgdbId(igdbId);
    }

    @GetMapping("/games")
    public Iterable<Game> getAll() {
        return gameService.getAllGames();
    }

    @PostMapping("/game")
    public Game add(@RequestBody GameForm gameForm) {
        return gameService.addGame(gameForm);
    }

    @DeleteMapping("/game/{id}")
    public void delete(@PathVariable("id") Long id) {
        gameService.deleteGame(id);
    }

    @PutMapping("/game/{id}")
    public void put(@PathVariable("id") Long id, @RequestBody GameUpdateDTO gameUpdateDTO) {
        gameService.updateGame(id, gameUpdateDTO);
    }

    @PatchMapping("/game/{id}")
    public void patch(@PathVariable("id") Long id, @RequestBody Map<String, Enums.status> requestBody) {
        Enums.status status = requestBody.get("status");
        gameService.patchGame(id, status);
    }

    @GetMapping("/checkEntity/{id}")
    public boolean isContains(@PathVariable("id") Long id) {
        return gameService.isContains(id);
    }
}
