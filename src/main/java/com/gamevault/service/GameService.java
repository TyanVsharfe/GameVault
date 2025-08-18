package com.gamevault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.exception.GameNotFoundInIgdbException;
import com.gamevault.exception.IgdbFetchException;
import com.gamevault.exception.IgdbParsingException;
import com.gamevault.dto.output.igdb.IgdbGameDTO;
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

            List<IgdbGameDTO> games = new ObjectMapper().readValue(igdbGameJson, new TypeReference<>() {});

            if (games == null || games.isEmpty()) {
                throw new GameNotFoundInIgdbException("Game with id " + igdbId + " not found in IGDB.");
            }

            IgdbGameDTO igdbGameDto = games.get(0);

            Game game = new Game();
            game.setIgdbId(igdbGameDto.id());
            game.setTitle(igdbGameDto.name());
            game.setDescription(igdbGameDto.summary());
            game.setCoverUrl(igdbGameDto.cover().url());

            return gameRepository.save(game);

        } catch (UnirestException e) {
            throw new IgdbFetchException("Failed to fetch IGDB game", e);
        } catch (JsonProcessingException e) {
            throw new IgdbParsingException("Failed to parse IGDB game JSON", e);
        }
    }

    @Transactional
    public void delete(Long id) {
        gameRepository.deleteById(id);
    }
}
