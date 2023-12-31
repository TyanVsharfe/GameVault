package com.gamevault.controller;

import com.gamevault.db.repository.GameRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RESTController {

    final GameRepository gameRepository;

    public RESTController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("/game/{id}")
    public int getGame(@PathVariable("id") Long id) {
        return 0;
    }

    @GetMapping("/games")
    public int getGames() {
        return 0;
    }

    @PostMapping("/game")
    public int postGame() {
        return 0;
    }

    @DeleteMapping("/game/{id}")
    public int deleteGame(@PathVariable("id") Long id) {
        return 0;
    }

    @PutMapping("/game/{id}")
    public int putGame(@PathVariable("id") Long id) {
        return 0;
    }
}
