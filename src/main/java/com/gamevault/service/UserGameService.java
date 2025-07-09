package com.gamevault.service;

import com.gamevault.data_template.Enums;
import com.gamevault.data_template.UserStatisticsInfo;
import com.gamevault.db.model.Game;
import com.gamevault.db.model.Note;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.form.UserGameUpdateDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserGameService {
    private final UserGameRepository userGameRepository;
    private final GameRepository gameRepository;
    private final GameService gameService;

    public UserGameService(UserGameRepository userGameRepository, GameRepository gameRepository, GameService gameService) {
        this.userGameRepository = userGameRepository;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    public Iterable<UserGame> getAll(String status, User author) {
        if (status == null || status.isEmpty()) {
            return userGameRepository.findGamesByUser_Username(author.getUsername());
        }
        else {
            try {
                Enums.status statusEnum = Enums.status.valueOf(status);
                return userGameRepository.findGamesByStatusAndUser_Username(statusEnum, author.getUsername());
            }
            catch (IllegalArgumentException e) {
                throw new EntityNotFoundException("Invalid game status " + e.getMessage());
            }
        }
    }

    public Iterable<Long> getAllIgdbIds(String status) {
        Iterable<UserGame> allGames;

        if (status == null || status.isEmpty()) {
            allGames = userGameRepository.findAll();
        }
        else {
            try {
                Enums.status statusEnum = Enums.status.valueOf(status);
                allGames = userGameRepository.findGamesByStatus(statusEnum);
            }
            catch (IllegalArgumentException e) {
                throw new EntityNotFoundException("Invalid game status " + e.getMessage());
            }
        }

        LinkedList<Long> igdbIds = new LinkedList<>();

        for (UserGame userGame : allGames) {
            igdbIds.add(userGame.getId());
        }

        return igdbIds;
    }

    public Optional<UserGame> get(Long id) {
        return userGameRepository.findById(id);
    }

    public Optional<UserGame> getByIgdbId(Long igdbId) {
        return userGameRepository.findUserGameByGame_IgdbId(igdbId);
    }

    @Transactional
    public UserGame add(Long igdbId, User author) {
        log.info("Attempting to add game with igdbId={} for user '{}'", igdbId, author.getUsername());
        Optional<UserGame> userGame = userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, author.getUsername());
        if (userGame.isPresent()) {
            log.warn("Game with igdbId={} is already added for user '{}'", igdbId, author.getUsername());
            throw new EntityExistsException("Game already added");
        }
        Optional<Game> game = gameRepository.findById(igdbId);
        if (game.isEmpty()) {
            log.info("Game with igdbId={} not found in the local database, attempting to fetch via GameService", igdbId);
            game = Optional.ofNullable(gameService.add(igdbId));
            if (game.isEmpty()) {
                log.error("Game with igdbId={} could not be found locally or via GameService", igdbId);
                throw new IllegalArgumentException("Game not found");
            }
            else {
                log.info("Game with igdbId={} successfully fetched via GameService", igdbId);
            }
        }

        UserGame saved = userGameRepository.save(new UserGame(author, game.get()));
        log.info("Game with igdbId={} successfully added for user '{}'", igdbId, author.getUsername());
        return saved;
    }

    @Transactional
    public UserGame update(Long igdbId, User author, UserGameUpdateDTO userGameUpdateDTO) {
        log.info("Attempting to update UserGame with id={} using data: status={}, rating={}, notes={}",
                igdbId,
                userGameUpdateDTO.status().orElse(null),
                userGameUpdateDTO.userRating().orElse(null),
                userGameUpdateDTO.notes().map(List::size).orElse(0));

        UserGame userGame = userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, author.getUsername())
                .orElseThrow(() -> {
                    log.error("UserGame with id={} not found", igdbId);
                    return new EntityNotFoundException("UserGame not found with igdbId: " + igdbId);
                });

        log.info("Found UserGame with id={} for user '{}', game title='{}'",
                userGame.getId(),
                userGame.getUser().getUsername(),
                userGame.getGame().getTitle());

        userGame.setStatus(userGameUpdateDTO.status().orElse(userGame.getStatus()));
        userGame.setUserRating(userGameUpdateDTO.userRating().orElse(userGame.getUserRating()));

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        userGameUpdateDTO.notes().ifPresent(notes -> {
            for (Note note : notes) {
                note.setUserGame(userGame);
                userGame.getNotes().add(note);
                log.info("Added note for UserGame id={}: {}", userGame.getId(), note.getContent());
            }
        });

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public void delete(Long igdbId, User author) {
        Optional<UserGame> userGame = userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, author.getUsername());
        log.info("Found: {}", userGame.isPresent());
        log.info("Deleting UserGame with IGDB ID {} for user '{}'", igdbId, author.getUsername());
        int deleted = userGameRepository.deleteUserGameByGame_IgdbIdAndUser_Username(igdbId, author.getUsername());

        if (deleted == 1) {
            log.info("Successfully deleted UserGame with IGDB ID {} for user '{}'", igdbId, author.getUsername());
        } else {
            log.warn("No UserGame found to delete with IGDB ID {} for user '{}'", igdbId, author.getUsername());
            throw new IllegalArgumentException("UserGame not found. Check your IgdbId or User");
        }
    }


    public boolean isContains(Long igdbId, User author) {
        return userGameRepository.existsByGame_IgdbIdAndUser_Username(igdbId, author.getUsername());
    }

    public UserStatisticsInfo userStatistics() {
        UserStatisticsInfo userInfo = new UserStatisticsInfo();
        userInfo.setTotalGames(userGameRepository.count());
        userInfo.setCompletedGames(userGameRepository.countGamesByStatus(Enums.status.Completed));
        userInfo.setPlayingGames(userGameRepository.countGamesByStatus(Enums.status.Playing));
        userInfo.setPlannedGames(userGameRepository.countGamesByStatus(Enums.status.Planned));
        userInfo.setAbandonedGames(userGameRepository.countGamesByStatus(Enums.status.Abandoned));
        userInfo.setNoneStatusGames(userGameRepository.countGamesByStatus(Enums.status.None));
        return userInfo;
    }
}
