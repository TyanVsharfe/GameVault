package com.gamevault.controller;

import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.form.GameForm;
import com.gamevault.service.GameService;
import org.springframework.web.bind.annotation.*;

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
    public int put(@PathVariable("id") Long id) {
        return 0;
    }
}
