package com.gamevault.db.repository;

import com.gamevault.db.model.UserGameList;
import com.gamevault.db.model.User;
import com.gamevault.dto.output.enriched.GameListReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGameListRepository extends CrudRepository<UserGameList, UUID> {
    Page<UserGameList> findByAuthor(User user, Pageable pageable);
    List<UserGameList> findByIsPublicTrueAndAuthorUsername(String username);
    @Query("""
           SELECT NEW com.gamevault.dto.output.enriched.GameListReference(
               ugl.uuid,
               ugl.name,
               ugl.isPublic
           )
           FROM UserGameListItem ugli
           JOIN ugli.userGameList ugl
           WHERE ugli.game.igdbId = :userGameId
           AND ugl.author.username = :username
           """)
    List<GameListReference> findListsByUserGameIdAndAuthorUsername(Long userGameId, String username);

    @EntityGraph(attributePaths = {"items", "items.game", "author"})
    @Query("SELECT ugl FROM UserGameList ugl WHERE ugl.uuid = :listId")
    Optional<UserGameList> findByIdWithItems(UUID listId);

    boolean existsByUuidAndAuthor(UUID listId, User user);
}
