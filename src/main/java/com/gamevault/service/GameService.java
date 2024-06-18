package com.gamevault.service;

import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.form.GameUpdateDTO;
import com.gamevault.form.GameForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Optional;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Iterable<Long> getAllGamesIgdbIds() {
        Iterable<Game> allGames = gameRepository.findAll();
        LinkedList<Long> igdbIds = new LinkedList<>();

        for (Game game : allGames) {
            igdbIds.add(game.getIgdbId());
        }

        return igdbIds;
    }

    public Optional<Game> getGame(Long id) {
        return gameRepository.findById(id);
    }
    public Optional<Game> getGameByIgdbId(Long id) {
        return gameRepository.findGameByIgdbId(id);
    }

    public Game addGame(GameForm gameForm) {
        System.out.println(gameForm.coverUrl() + "  title " + gameForm.title());
        return gameRepository.save(new Game(gameForm));
    }

    public void updateGame(Long id, GameUpdateDTO gameUpdateDTO) {
        Game game = gameRepository.findGameByIgdbId(id).orElseThrow(
                () -> new EntityNotFoundException("Game with id " + gameUpdateDTO.id() + " not found"));
        System.out.println("Id "+ game.getId() + " Title " + game.getTitle() + " Rating " + game.getUserRating() + " Status " + game.getStatus());
        System.out.println("DTO id " + gameUpdateDTO.id() + " Rating " + gameUpdateDTO.userRating());

        game.setStatus(gameUpdateDTO.status().orElse(game.getStatus()));
        game.setUserRating(gameUpdateDTO.userRating().orElse(game.getUserRating()));

        if (gameUpdateDTO.notes().isPresent())
        {
            gameUpdateDTO.notes().ifPresent(notes -> {
                notes.forEach(note -> {
                    note.setGame(game);
                    game.getNotes().add(note);
                });
            });

            System.out.println("Note content " + game.getNotes().get(0));
        }

        System.out.println("Запись изменена");
        System.out.println("Id "+ game.getId() + " Title " + game.getTitle() + " Rating " + game.getUserRating() + " Status " + game.getStatus());
        gameRepository.save(game);
    }

    @Transactional
    public void deleteGame(Long id) {
        gameRepository.deleteByIgdbId(id);
    }

    public boolean isContains(Long id) {
        return gameRepository.existsByIgdbId(id);
    }
}
