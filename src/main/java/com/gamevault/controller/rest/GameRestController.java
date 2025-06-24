package com.gamevault.controller.rest;

import com.gamevault.data_template.UserStatisticsInfo;
import com.gamevault.db.model.Game;
import com.gamevault.form.GameForm;
import com.gamevault.form.GameUpdateDTO;
import com.gamevault.service.GameService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class GameRestController {
    private final GameService gameService;

    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/game/{id}")
    public Optional<Game> get(@PathVariable("id") Long id) {
        return gameService.getGame(id);
    }

    @GetMapping("/games")
    public Iterable<Game> getAll(@RequestParam(value = "status", required = false) String status) {
        return gameService.getAllGames(status);
    }

    @GetMapping("/games/ids")
    public Iterable<Long> getAllIds(@RequestParam(value = "status", required = false) String status) {
        return gameService.getAllGamesIgdbIds(status);
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

    @GetMapping("/checkEntity/{id}")
    public boolean isContains(@PathVariable("id") Long id) {
        return gameService.isContains(id);
    }

    @GetMapping("/statistics")
    public UserStatisticsInfo userStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /statistics: " + authentication.getName());
        return gameService.userStatistics();
    }
}
