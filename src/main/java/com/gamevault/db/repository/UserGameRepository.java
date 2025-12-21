package com.gamevault.db.repository;

import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.UserModeDto;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.UserGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserGameRepository extends CrudRepository<UserGame, Long> {
    boolean existsByGame_IgdbIdAndUser_Username(Long igdbId, String username);
    Optional<UserGame> findUserGameByGame_IgdbId(Long igdbId);
    @Query(
        """
        SELECT new com.gamevault.dto.output.db.UserGameBaseData(
            ug.id,
            ug.status,
            ug.userRating,
            ug.review,
            ug.isFullyCompleted,
            ug.isOverallRatingManual,
            ug.isOverallStatus,
            ug.userCoverUrl,
            ug.createdAt,
            ug.updatedAt,
            COUNT(DISTINCT n)
        )
        FROM UserGame ug
        LEFT JOIN ug.notes n
        WHERE ug.user.username = :username
          AND ug.game.igdbId = :igdbId
        GROUP BY ug.id
    """)
    Optional<UserGameBaseData> getUserGameBaseDataByUsername(Long igdbId, String username);
    @Query("""
            SELECT new com.gamevault.dto.output.enriched.UserModeDto(
                m.id,
                m.mode,
                m.status,
                m.userRating
            )
            FROM UserGameMode m
            WHERE m.userGame.id = :userGameId
           """)
    List<UserModeDto> findUserModes(Long userGameId);
    Optional<UserGame> findUserGameByGame_IgdbIdAndUser_Username(Long igdbId, String username);
    Optional<UserGame> findUserGameByGame_IgdbIdAndUser_Id(Long igdbId, UUID userId);
    Iterable<UserGame> findGamesByStatus(Enums.Status status);
    Page<UserGame> findGamesByStatusAndUser_Username(Enums.Status status, String username, Pageable pageable);
    Page<UserGame> findGamesByUser_Username(String username, Pageable pageable);
    List<UserGame> findByGameIgdbIdAndReviewIsNotNull(Long igdbId);
    long countGamesByStatus(Enums.Status status);
    long countGamesByUser_Username(String username);
    long countGamesByStatusAndUser_Username(Enums.Status status, String username);
    long countGamesByStatusAndUser_Id(Enums.Status status, UUID userId);
    int deleteUserGameByGame_IgdbIdAndUser_Username(Long igdbId, String username);

    @Query("SELECT ug.game.igdbId FROM UserGame ug WHERE ug.user.id = :userId AND ug.status = com.gamevault.enums.Enums.Status.COMPLETED")
    Set<Long> findCompletedGameIdsByUserId(UUID userId);

    @Query("SELECT AVG(ug.userRating) FROM UserGame ug WHERE ug.user.username = :username AND ug.userRating IS NOT NULL")
    Double calculateAverageRatingByUsername(String username);

    @Query("SELECT COUNT(n) FROM Note n WHERE n.user.username = :username")
    long countNotesByUsername(String username);

    @Query("SELECT ug FROM UserGame ug " +
            "LEFT JOIN FETCH ug.game g " +
            "LEFT JOIN FETCH ug.userModes um " +
            "LEFT JOIN FETCH ug.dlcs " +
            "WHERE ug.user.username = :username AND g.igdbId IN :igdbIds")
    List<UserGame> findByUserUsernameAndGameIgdbIdIn(String username, Set<Long> igdbIds);
    @Query(
            """
            SELECT new com.gamevault.dto.output.db.UserGameBatchData(
                ug.id,
                g.igdbId,
                ug.status,
                ug.userRating,
                ug.review,
                ug.isFullyCompleted,
                ug.isOverallRatingManual,
                ug.isOverallStatus,
                ug.userCoverUrl,
                ug.createdAt,
                ug.updatedAt,
                COUNT(DISTINCT n)
            )
            FROM UserGame ug
            JOIN ug.game g
            LEFT JOIN ug.notes n
            WHERE ug.user.username = :username
              AND ug.game.igdbId IN :igdbIds
            GROUP BY ug.id
        """)
    List<UserGameBatchData> getUserGamesBaseDataByUsername(String username, Set<Long> igdbIds);
}
