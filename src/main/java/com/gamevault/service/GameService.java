package com.gamevault.service;

import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.form.GameForm;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {
    final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGame(Long id) {
        return gameRepository.findById(id);
    }

    public Game addGame(GameForm gameForm) {
        System.out.println(gameForm.coverUrl() + "  title " + gameForm.title());
        return gameRepository.save(new Game(gameForm));
    }

    public void updateGame(Game game) {
        gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    public boolean isContains(Long id) {
        return gameRepository.existsByIgdbId(id);
    }
}
