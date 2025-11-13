package com.gamevault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.dto.input.GameForm;
import com.gamevault.dto.output.igdb.GameMode;
import com.gamevault.enums.Enums;
import com.gamevault.exception.GameNotFoundInIgdbException;
import com.gamevault.exception.IgdbFetchException;
import com.gamevault.exception.IgdbParsingException;
import com.gamevault.dto.output.igdb.IgdbGameDTO;
import com.mashape.unirest.http.exceptions.UnirestException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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

            if (igdbGameDto.game_modes() == null) {
                log.info("Game with igdbId={} cannot be added to database because haven't game modes", igdbGameDto.id());
                throw new IllegalArgumentException("Game with igdbId=" + igdbGameDto.id() + " cannot be added to database because haven't game modes");
            }
            List<Enums.GameModesIGDB> modes = new ArrayList<>();
            for (GameMode mode: igdbGameDto.game_modes()) {
                modes.add(Enums.GameModesIGDB.fromJson(mode.slug()));
            }

            Game game = new Game(new GameForm(
                    igdbGameDto.id(), igdbGameDto.name(), igdbGameDto.cover().url(),
                    igdbGameDto.summary(), Enums.CategoryIGDB.fromNumber(igdbGameDto.game_type().id()), modes));
            Game saved = gameRepository.save(game);
            log.info("Game with igdbId={} successfully added", igdbId);

            log.info("Game with igdbId={} have dlcs:{}", igdbId, igdbGameDto.dlcs());
            if (igdbGameDto.dlcs() != null && !igdbGameDto.dlcs().isEmpty()) {
                List<Game> dlcs = new ArrayList<>();
                for (IgdbGameDTO igdbDlc: igdbGameDto.dlcs()) {
                    Optional<Game> dbDlc = gameRepository.findById(igdbDlc.id());
                    if (dbDlc.isPresent()) {
                        log.warn("DLC with igdbId={} is already added in the local database", igdbDlc.id());
                        continue;
                    }
                    log.info("DLC with igdbId={} not found in the local database, attempting to add via GameService", igdbDlc.id());

                    modes.clear();
                    if (igdbDlc.game_modes() == null) {
                        log.info("DLC with igdbId={} cannot be added to database because haven't game modes", igdbDlc.id());
                        continue;
                    }
                    for (GameMode mode: igdbDlc.game_modes()) {
                        modes.add(Enums.GameModesIGDB.fromJson(mode.slug()));
                    }

                    Game dlc = new Game(new GameForm(
                            igdbDlc.id(), igdbDlc.name(), igdbDlc.cover().url(),
                            igdbDlc.summary(), Enums.CategoryIGDB.fromNumber(igdbDlc.game_type().id()), modes), saved);
                    Game savedDlc = gameRepository.save(dlc);
                    dlcs.add(savedDlc);
                    log.info("DLC with igdbId={} successfully added", igdbDlc.id());
                }
                saved.addDlcs(dlcs);
            }
            log.info("Game with igdbId={} have expansions:{}", igdbId, igdbGameDto.expansions());
            if (igdbGameDto.expansions() != null && !igdbGameDto.expansions().isEmpty()) {
                List<Game> expansions = new ArrayList<>();
                for (IgdbGameDTO igdbExp: igdbGameDto.expansions()) {
                    Optional<Game> dbExpansion = gameRepository.findById(igdbExp.id());
                    if (dbExpansion.isPresent()) {
                        log.warn("Expansion with igdbId={} is already added in the local database", igdbExp.id());
                        continue;
                    }
                    log.info("Expansion with igdbId={} not found in the local database, attempting to add via GameService", igdbExp.id());

                    modes.clear();
                    if (igdbExp.game_modes() == null) {
                        log.info("Expansion with igdbId={} cannot be added to database because haven't game modes", igdbExp.id());
                        continue;
                    }
                    for (GameMode mode: igdbExp.game_modes()) {
                        modes.add(Enums.GameModesIGDB.fromJson(mode.slug()));
                    }

                    Game expansion = new Game(new GameForm(
                            igdbExp.id(), igdbExp.name(), igdbExp.cover().url(),
                            igdbExp.summary(), Enums.CategoryIGDB.fromNumber(igdbExp.game_type().id()), modes), saved);
                    Game savedExp = gameRepository.save(expansion);
                    expansions.add(savedExp);
                    log.info("Expansion with igdbId={} successfully added", igdbExp.id());
                }
                saved.addDlcs(expansions);
            }
            return gameRepository.save(saved);
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
