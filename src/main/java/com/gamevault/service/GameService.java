package com.gamevault.service;

import com.gamevault.db.model.Game;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.dto.input.GameForm;
import com.gamevault.dto.output.igdb.GameMode;
import com.gamevault.enums.Enums;
import com.gamevault.exception.GameNotFoundInIgdbException;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.IgdbGameService;
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
    public Game getOrCreate(Long igdbId) {
        Optional<Game> existingGame = gameRepository.findById(igdbId);
        if (existingGame.isPresent()) {
            log.debug("Game with igdbId={} found in database", igdbId);
            return existingGame.get();
        }

        log.info("Game with igdbId={} not found in database, fetching from IGDB", igdbId);
        return add(igdbId);
    }

    @Transactional
    public List<Game> getOrCreateBatch(List<Long> igdbIds) {

        List<Game> existingGames = (List<Game>) gameRepository.findAllById(igdbIds);
        List<Game> result = new ArrayList<>(existingGames);

        List<Long> existingIds = existingGames.stream()
                .map(Game::getIgdbId)
                .toList();

        List<Long> missingIds = igdbIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            log.info("Found {} games missing in database, fetching from IGDB", missingIds.size());
            for (Long missingId : missingIds) {
                try {
                    Game game = add(missingId);
                    result.add(game);
                } catch (Exception e) {
                    log.error("Failed to add game with igdbId={}: {}", missingId, e.getMessage());
                }
            }
        }

        return result;
    }

    @Transactional
    public Game add(Long igdbId) {
        IgdbGameDto igdbGameDto = igdbGameService.getGame(igdbId);

        if (igdbGameDto == null) {
            throw new GameNotFoundInIgdbException("Game with id " + igdbId + " not found in IGDB.");
        }

        if (igdbGameDto.game_modes() == null) {
            log.info("Game with igdbId={} cannot be added to database because haven't game modes", igdbGameDto.id());
            throw new IllegalArgumentException("Game with igdbId=" + igdbGameDto.id() + " cannot be added to database because haven't game modes");
        }

        List<Enums.GameModesIGDB> modes = extractGameModes(igdbGameDto.game_modes());

        Game game = new Game(new GameForm(
                igdbGameDto.id(),
                igdbGameDto.name(),
                igdbGameDto.cover().url(),
                igdbGameDto.summary(),
                Enums.CategoryIGDB.fromNumber(igdbGameDto.game_type().id()), modes));
        Game saved = gameRepository.save(game);
        log.info("Game with igdbId={} successfully added", igdbId);

        log.info("Game with igdbId={} have dlcs:{}", igdbId, igdbGameDto.dlcs());
        if (igdbGameDto.dlcs() != null && !igdbGameDto.dlcs().isEmpty()) {
            List<Game> dlcs = processAdditionalContent(igdbGameDto.dlcs(), saved, "DLC");
            saved.addDlcs(dlcs);
        }

        log.info("Game with igdbId={} have expansions:{}", igdbId, igdbGameDto.expansions());
        if (igdbGameDto.expansions() != null && !igdbGameDto.expansions().isEmpty()) {
            List<Game> expansions = processAdditionalContent(igdbGameDto.expansions(), saved, "Expansion");
            saved.addDlcs(expansions);
        }

        return gameRepository.save(saved);
    }

    private List<Game> processAdditionalContent(List<IgdbGameDto> items, Game parentGame, String type) {
        List<Game> result = new ArrayList<>();

        for (IgdbGameDto item : items) {
            Optional<Game> existing = gameRepository.findById(item.id());
            if (existing.isPresent()) {
                log.warn("{} with igdbId={} is already in the database", type, item.id());
                continue;
            }

            if (item.game_modes() == null) {
                log.info("{} with igdbId={} skipped: no game modes", type, item.id());
                continue;
            }

            log.info("{} with igdbId={} not found in database, adding", type, item.id());

            List<Enums.GameModesIGDB> modes = extractGameModes(item.game_modes());

            Game dlcOrExpansion = new Game(new GameForm(
                    item.id(),
                    item.name(),
                    item.cover().url(),
                    item.summary(),
                    Enums.CategoryIGDB.fromNumber(item.game_type().id()),
                    modes),
                    parentGame);

            Game saved = gameRepository.save(dlcOrExpansion);
            result.add(saved);
            log.info("{} with igdbId={} successfully added", type, item.id());
        }

        return result;
    }

    private List<Enums.GameModesIGDB> extractGameModes(List<GameMode> gameModes) {
        List<Enums.GameModesIGDB> modes = new ArrayList<>();
        for (GameMode mode : gameModes) {
            modes.add(Enums.GameModesIGDB.fromJson(mode.slug()));
        }
        return modes;
    }

    @Transactional
    public void delete(Long id) {
        gameRepository.deleteById(id);
    }

    private boolean isValidDLC(IgdbGameDto dlc) {
        String title = dlc.name().toLowerCase();

        String[] invalidKeywords = {
                "skin", "costume", "outfit", "appearance",
                "weapon pack", "weapon set",
                "premium", "bundle", "deluxe",
                "ultimate pack", "gold pack",
                "cosmetic"
        };

        for (String keyword: invalidKeywords) {
            if (title.contains(keyword)) {
                return false;
            }
        }

        return !title.matches(".*(hero|character)\\s*$") &&
                !title.matches(".*\\s+(hero|character)\\s*$");
    }

    public boolean exists(Long igdbId) {
        return gameRepository.existsById(igdbId);
    }
}
