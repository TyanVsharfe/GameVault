package com.gamevault.service;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.Game;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.repository.GameRepository;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.events.UserGameCompletedEvent;
import com.gamevault.dto.input.update.UserGameUpdateForm;
import com.gamevault.dto.output.UserReviewsDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final GameRepository gameRepository;
    private final GameService gameService;
    private final ApplicationEventPublisher eventPublisher;

    public UserGameService(UserGameRepository userGameRepository, UserService userService, GameRepository gameRepository,
                           GameService gameService, ApplicationEventPublisher eventPublisher) {
        this.userGameRepository = userGameRepository;
        this.userService = userService;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.eventPublisher = eventPublisher;
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

    public List<UserReviewsDTO> getGameReviews(Long igdbId) {
        List<UserGame> reviews = userGameRepository.findByGameIgdbIdAndReviewIsNotNull(igdbId);
        return reviews.stream()
                .filter(review -> !review.getReview().isEmpty())
                .map(UserReviewsDTO::new)
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
        Optional<Game> game = gameRepository.findById(igdbId);
        if (game.isEmpty()) {
            log.info("Game with igdbId={} not found in the local database, attempting to fetch via GameService", igdbId);
            game = Optional.ofNullable(gameService.add(igdbId));
            if (game.isEmpty()) {
                log.error("Game with igdbId={} could not be found locally or via GameService", igdbId);
                return null;
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
        log.info("Attempting to update UserGame with id={} using data: status={}, rating={}, notes={}",
                igdbId,
                userGameUpdateForm.status(),
                userGameUpdateForm.userRating(),
                userGameUpdateForm.note());

        UserGame userGame = findByUserUsernameAndIgdbId(igdbId, user);

        log.info("Found UserGame with id={} for user '{}', game title='{}'",
                userGame.getId(),
                userGame.getUser().getUsername(),
                userGame.getGame().getTitle());

        userGame.updateDto(userGameUpdateForm);

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        if (saved.getStatus().equals(Enums.status.Completed)) {
            eventPublisher.publishEvent(new UserGameCompletedEvent(user, userGame));
        }

        return saved;
    }

    @Transactional
    public UserGame updateStatus(Long igdbId, User user, Enums.status status) {
        UserGame userGame = findByUserUsernameAndIgdbId(igdbId, user);
        userGame.setStatus(status);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated status for UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

        if (saved.getStatus().equals(Enums.status.Completed)) {
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
    public UserGame updateRating(Long gameId, User user, Double rating) {
        UserGame userGame = findByUserUsernameAndIgdbId(gameId, user);
        userGame.setUserRating(rating);

        ZoneId zoneId = ZoneId.systemDefault();
        OffsetDateTime offsetDateTime = OffsetDateTime.now(zoneId);
        userGame.setUpdatedAt(offsetDateTime.toInstant());

        UserGame saved = userGameRepository.save(userGame);
        log.info("Successfully updated rating for UserGame with id={} for user '{}'", saved.getId(), saved.getUser().getUsername());

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
}
