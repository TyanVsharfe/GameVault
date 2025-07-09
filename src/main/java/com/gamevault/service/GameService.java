package com.gamevault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.form.igdb.GameDTO;
import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final IgdbGameService igdbGameService;

    public GameService(GameRepository gameRepository, IgdbGameService igdbGameService) {
        this.gameRepository = gameRepository;
        this.igdbGameService = igdbGameService;
    }

    public Optional<Game> get(Long id) {
        return gameRepository.findById(id);
    }

    @Transactional
    public Game add(Long igdbId) {
        try {
            String igdbGameJson = igdbGameService.gameIGDB(igdbId.toString());

            List<GameDTO> games = new ObjectMapper().readValue(igdbGameJson, new TypeReference<>() {});

            if (games == null || games.isEmpty()) {
                throw new IllegalStateException("Game with id " + igdbId + " not found in IGDB.");
            }

            GameDTO gameDto = games.get(0);

            Game game = new Game();
            game.setIgdbId((long) gameDto.id());
            game.setTitle(gameDto.name());
            game.setDescription(gameDto.summary());
            game.setCoverUrl(gameDto.cover().url());

            return gameRepository.save(game);

        } catch (UnirestException e) {
            throw new RuntimeException("Failed to fetch IGDB game", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(Long id) {
        gameRepository.deleteById(id);
    }
}
