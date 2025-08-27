package com.gamevault.db.repository;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.UserGame;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserGameRepository extends CrudRepository<UserGame, Long> {
    boolean existsByGame_IgdbIdAndUser_Username(Long IgdbId, String username);
    Optional<UserGame> findUserGameByGame_IgdbId(Long IgdbId);
    Optional<UserGame> findUserGameByGame_IgdbIdAndUser_Username(Long IgdbId, String username);
    Optional<UserGame> findUserGameByGame_IgdbIdAndUser_Id(Long IgdbId, UUID userId);
    Iterable<UserGame> findGamesByStatus(Enums.status status);
    Iterable<UserGame> findGamesByStatusAndUser_Username(Enums.status status, String username);
    Iterable<UserGame> findGamesByUser_Username(String username);
    List<UserGame> findByGameIgdbIdAndReviewIsNotNull(Long IgdbId);
    long countGamesByStatus(Enums.status status);
    long countGamesByUser_Username(String username);
    long countGamesByStatusAndUser_Username(Enums.status status, String username);
    long countGamesByStatusAndUser_Id(Enums.status status, UUID userId);
    int deleteUserGameByGame_IgdbIdAndUser_Username(Long IgdbId, String username);

    @Query("SELECT ug.game.igdbId FROM UserGame ug WHERE ug.user.id = :userId AND ug.status = com.gamevault.enums.Enums.status.Completed")
    Set<Long> findCompletedGameIdsByUserId(UUID userId);
}
