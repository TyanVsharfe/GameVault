package com.gamevault.db.repository;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    boolean existsByIgdbId(Long igdbId);
    void deleteByIgdbId(Long igdbId);
    Optional<Game> findGameByIgdbId(Long igdbId);
    Iterable<Game> findGamesByStatus(Enums.status status);
    long countGamesByStatus(Enums.status status);
}
