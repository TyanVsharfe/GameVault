package com.gamevault.db.repository;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.UserGame;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGameRepository extends CrudRepository<UserGame, Long> {
    boolean existsByGame_IgdbIdAndUser_Username(Long IgdbId, String username);
    Optional<UserGame> findUserGameByGame_IgdbId(Long IgdbId);
    Optional<UserGame> findUserGameByGame_IgdbIdAndUser_Username(Long IgdbId, String username);
    Iterable<UserGame> findGamesByStatus(Enums.status status);
    Iterable<UserGame> findGamesByStatusAndUser_Username(Enums.status status, String username);
    Iterable<UserGame> findGamesByUser_Username(String username);
    long countGamesByStatus(Enums.status status);
    int deleteUserGameByGame_IgdbIdAndUser_Username(Long IgdbId, String username);
}
