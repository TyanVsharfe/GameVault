package com.gamevault.db.repository;

import com.gamevault.db.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    boolean existsByIgdbId(Long IgdbId);
    void deleteByIgdbId(Long id);
}
