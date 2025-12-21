package com.gamevault.service;

import com.gamevault.db.model.Game;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserGameListItemRepository;
import com.gamevault.db.repository.UserGameListRepository;
import com.gamevault.dto.input.UserGameListForm;
import com.gamevault.dto.input.update.UserGameListUpdateForm;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserGameListService {
    private final UserGameListRepository userGameListRepository;
    private final UserGameListItemRepository userGameListItemRepository;
    private final GameService gameService;

    public UserGameListService(UserGameListRepository userGameListRepository, UserGameListItemRepository userGameListItemRepository, GameService gameService) {
        this.userGameListRepository = userGameListRepository;
        this.userGameListItemRepository = userGameListItemRepository;
        this.gameService = gameService;
    }
    
    public UserGameList createList(UserGameListForm userGameListForm, User author) {
        UserGameList userGameList = new UserGameList(author, userGameListForm);
        UserGameList saved = userGameListRepository.save(userGameList);
        log.info("GameList with name={} successfully added for user '{}'", saved.getName(), saved.getAuthor().getUsername());
        return saved;
    }

    @Transactional
    public UserGameList addGamesToList(UserGameList userGameList, List<Long> igdbIds) {
        List<Game> games = gameService.getOrCreateBatch(igdbIds);

        int order = userGameList.getItems().size();
        log.info("GameList with id={} will receive '{}' games", userGameList.getUuid(), games.size());
        for (Game game : games) {
            boolean exists = userGameList.getItems().stream()
                    .anyMatch(item -> item.getGame().getIgdbId().equals(game.getIgdbId()));

            if (!exists) {
                userGameList.addGame(game, order++);
                log.info("GameListItem with igdbId={} successfully added for user '{}'", game.getIgdbId(), userGameList.getAuthorUsername());
            }
        }

        return userGameListRepository.save(userGameList);
    }

    @Transactional
    public void removeGameFromList(UUID listId, Long igdbId, User user) {
        UserGameList userGameList = getGameListById(listId, user);
        validateOwnership(userGameList, user);

        Game game = gameService.getOrCreate(igdbId);
        userGameList.removeGame(game);

        userGameListRepository.save(userGameList);
    }

    @Transactional
    public void removeGamesFromList(UUID listId, List<Long> igdbIds, User user) {
        UserGameList userGameList = getGameListById(listId, user);
        validateOwnership(userGameList, user);

        for (long igdbId: igdbIds) {
            Game game = gameService.getOrCreate(igdbId);
            userGameList.removeGame(game);
        }

        userGameListRepository.save(userGameList);
    }

    @Transactional
    public UserGameList updateGameList(UUID listId, UserGameListUpdateForm form, User user) {
        UserGameList userGameList = getGameListById(listId, user);
        validateOwnership(userGameList, user);

        if (form.name() != null) {
            userGameList.setName(form.name());
        }
        if (form.description() != null) {
            userGameList.setDescription(form.description());
        }
        if (form.isPublic() != null) {
            userGameList.setPublic(form.isPublic());
        }

        if (form.games() != null && !form.games().isEmpty()) {
            addGamesToList(userGameList, form.games());
        }

        userGameList.updateTimestamp();
        return userGameListRepository.save(userGameList);
    }

    @Transactional
    public UserGameList copyGameList(UUID listId, User user) {
        UserGameList originalList = getGameListById(listId,user);

        if (!originalList.isPublic() && !originalList.isOwnedBy(user)) {
            throw new AccessDeniedException("This list is private");
        }

        UserGameList copiedList = new UserGameList(user, originalList);
        copiedList = userGameListRepository.save(copiedList);

        List<Long> gameIds = originalList.getItems().stream()
                .map(item -> item.getGame().getIgdbId())
                .toList();

        return addGamesToList(copiedList, gameIds);
    }

    @Transactional(readOnly = true)
    public Page<UserGameList> getUserLists(User user, Pageable pageable) {
        return userGameListRepository.findByAuthor(user, pageable);
    }

    @Transactional(readOnly = true)
    public List<UserGameList> getPublicLists(String username) {
        return userGameListRepository.findByIsPublicTrueAndAuthorUsername(username);
    }

    @Transactional(readOnly = true)
    public UserGameList getGameListById(UUID listId, User user) {
        UserGameList list = userGameListRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("Game list not found"));
        if (!list.isPublic()) {
            validateOwnership(list, user);
        }
        return list;
    }

    @Transactional
    public void deleteGameList(UUID listId, User user)  {
        UserGameList userGameList = getGameListById(listId, user);
        validateOwnership(userGameList, user);
        userGameListRepository.delete(userGameList);
    }

    private void validateOwnership(UserGameList userGameList, User user) {
        if (!userGameList.isOwnedBy(user)) {
            throw new AccessDeniedException("You don't have permission to modify this list");
        }
    }
}
