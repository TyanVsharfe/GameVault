package com.gamevault.service;

import com.gamevault.db.repository.UserGameCustomRepository;
import com.gamevault.dto.input.UserGamesFilterParams;
import com.gamevault.dto.input.update.UserGameModeUpdateForm;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.Game;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.events.UserGameCompletedEvent;
import com.gamevault.dto.input.update.UserGameUpdateForm;
import com.gamevault.dto.output.UserReviewsDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserGameService {
    private final UserGameRepository userGameRepository;
    private final UserService userService;
    private final GameService gameService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserGameCustomRepository userGameCustomRepository;

    public UserGameService(UserGameRepository userGameRepository, UserService userService,
                           GameService gameService, ApplicationEventPublisher eventPublisher, UserGameCustomRepository userGameCustomRepository) {
        this.userGameRepository = userGameRepository;
        this.userService = userService;
        this.gameService = gameService;
        this.eventPublisher = eventPublisher;
        this.userGameCustomRepository = userGameCustomRepository;
    }

    public Page<UserGame> getAll(User author, Pageable pageable, UserGamesFilterParams filterParams) {
        return userGameCustomRepository.findGamesWithFilters(filterParams, author.getUsername(), pageable);
    }

    public List<UserReviewsDto> getGameReviews(Long igdbId) {
        List<UserGame> reviews = userGameRepository.findByGameIgdbIdAndReviewIsNotNull(igdbId);
        return reviews.stream()
                .filter(review -> !review.getReview().isEmpty())
                .map(UserReviewsDto::new)
                .toList();
    }

    public UserGame getByIgdbId(Long igdbId, User user) {
        return userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername()).orElseThrow(() ->
                new EntityNotFoundException("Game with igdbId " + igdbId + " not found"));
    }

    @Transactional
    public UserGame add(Long igdbId, User author) {
        log.info("Attempting to add game with igdbId={} for user '{}'", igdbId, author.getUsername());
        Optional<UserGame> userGame = userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, author.getUsername());
        if (userGame.isPresent()) {
            log.warn("Game with igdbId={} is already added for user '{}'", igdbId, author.getUsername());
            return userGame.get();
        }

        Game game = gameService.getOrCreate(igdbId);

        UserGame saved;
        if (game.getCategory() == Enums.CategoryIGDB.DLC || game.getCategory() == Enums.CategoryIGDB.EXPANSION) {
            Optional<UserGame> parentGame = userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(game.getParentGame().getIgdbId(), author.getUsername());
            if (parentGame.isPresent()) {
                saved = userGameRepository.save(new UserGame(author, game, parentGame.get()));
                log.warn("Game with igdbId={} is already added for user '{}'", igdbId, author.getUsername());
            }
            else {
                log.warn("DLC with igdbId={} not added because the user={} does not have a parent game", igdbId, author.getUsername());
                throw new EntityNotFoundException("DLC cannot be added if the parent game is not added");
            }
        }
        else {
            saved = userGameRepository.save(new UserGame(author, game));
        }

        log.info("Game with igdbId={} successfully added for user '{}'", igdbId, author.getUsername());
        return saved;
    }

    @Transactional
    public UserGame add(Long igdbId, UUID userId) {
        Optional<User> user = userService.getUser(userId);
        if (user.isPresent()) {
            return add(igdbId, user.get());
        }
        else {
            log.warn("User with UUID '{}' is not exists.", userId);
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public UserGame update(Long igdbId, User user, UserGameUpdateForm userGameUpdateForm) {
        log.info("Attempting to update UserGame with id={} using data: status={}, rating={}, platform={}, notes={}",
                igdbId,
                userGameUpdateForm.status(),
                userGameUpdateForm.userRating(),
                userGameUpdateForm.platform(),
                userGameUpdateForm.note());

        UserGame userGame = findByUserUsernameAndIgdbId(igdbId, user);

        log.info("Found UserGame with id={} for user '{}', game title='{}'",
                userGame.getId(),
                userGame.getUser().getUsername(),
                userGame.getGame().getTitle());

        userGame.updateDto(userGameUpdateForm);

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        if (saved.getStatus().equals(Enums.Status.COMPLETED)) {
            eventPublisher.publishEvent(new UserGameCompletedEvent(user, userGame));
        }

        return saved;
    }

    @Transactional
    public UserGame updateMode(Long gameId, User user, Enums.GameModesIGDB mode, UserGameModeUpdateForm updateForm) {
        UserGame userGame = findByUserUsernameAndIgdbId(gameId, user);

        if (!userGame.getGame().getGameModes().contains(mode)) {
            throw new IllegalArgumentException("The game has no mode: " + mode.name());
        }

        userGame.updateMode(mode, updateForm);

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated rating for mode {} in UserGame with id={} for user '{}'",
                mode.name(), saved.getId(), saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public UserGame updateStatus(Long igdbId, User user, Enums.Status status) {
        UserGame userGame = findByUserUsernameAndIgdbId(igdbId, user);
        userGame.setStatus(status);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated status for UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        if (saved.getStatus().equals(Enums.Status.COMPLETED)) {
            eventPublisher.publishEvent(new UserGameCompletedEvent(user, userGame));
        }

        return saved;
    }

    @Transactional
    public UserGame updateFullyCompleted(Long gameId, User user, Boolean fullyCompleted) {
        UserGame userGame = findByUserUsernameAndIgdbId(gameId, user);
        userGame.setFullyCompleted(fullyCompleted);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated isFullyCompleted for UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public UserGame updateOverallRating(Long gameId, User user, Double rating) {
        UserGame userGame = findByUserUsernameAndIgdbId(gameId, user);
        userGame.setOverallRating(rating);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated rating for UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public UserGame updateModeRating(Long gameId, User user, Enums.GameModesIGDB mode, Double rating) {
        UserGame userGame = findByUserUsernameAndIgdbId(gameId, user);

        if (!userGame.getGame().getGameModes().contains(mode)) {
            throw new IllegalArgumentException("The game has no mode: " + mode.name());
        }

        userGame.setModeRating(mode, rating);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated info for mode {} in UserGame with id={} for user '{}'",
                mode.name(), saved.getId(), saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public UserGame updateReview(Long gameId, User user, String review) {
        UserGame userGame = findByUserUsernameAndIgdbId(gameId, user);
        userGame.setReview(review);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated review for UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public void delete(Long igdbId, User user) {
        UserGame userGame = findByUserUsernameAndIgdbId(igdbId, user);
        log.info("Deleting UserGame with IGDB ID {} for user '{}'", igdbId, user.getUsername());
        int deleted = userGameRepository.deleteUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername());

        if (deleted == 1) {
            log.info("Successfully deleted UserGame with IGDB ID {} for user '{}'", igdbId, user.getUsername());
        } else {
            log.warn("No UserGame found to delete with IGDB ID {} for user '{}'", igdbId, user.getUsername());
            throw new IllegalArgumentException("UserGame not found.");
        }
    }

    private UserGame findByUserUsernameAndIgdbId(Long igdbId, User user) {
        return userGameRepository.findUserGameByGame_IgdbIdAndUser_Username(igdbId, user.getUsername())
                .orElseThrow(() -> {
                    log.error("UserGame with id={} not found", igdbId);
                    return new EntityNotFoundException("UserGame not found");
                });
    }

    public boolean isContains(Long igdbId, User user) {
        return userGameRepository.existsByGame_IgdbIdAndUser_Username(igdbId, user.getUsername());
    }

    private void validateStatusForMode(Enums.GameModesIGDB mode, Enums.Status status) {
        if (mode == Enums.GameModesIGDB.SINGLE_PLAYER) {
            if (status == Enums.Status.PLAYED) {
                throw new IllegalArgumentException(
                        "This status is unacceptable for SINGLE PLAYER mode. Acceptable: Completed, Playing, Planned, Abandoned");
            }
        }
        if (mode == Enums.GameModesIGDB.MULTIPLAYER) {
            if (status == Enums.Status.COMPLETED) {
                throw new IllegalArgumentException(
                        "This status is unacceptable for MULTIPLAYER mode. Acceptable: Played, Playing, Planned, Abandoned");
            }
        }
    }
}
