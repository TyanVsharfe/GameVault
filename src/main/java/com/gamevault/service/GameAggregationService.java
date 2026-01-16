package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.repository.UserGameCustomRepository;
import com.gamevault.db.repository.UserGameListRepository;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.*;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GameAggregationService {

    private final IgdbGameService igdbService;
    private final UserGameRepository userGameRepository;
    private final UserGameCustomRepository userGameCustomRepository;
    private final UserGameListRepository userGameListRepository;

    public GameAggregationService(IgdbGameService igdbService, UserGameRepository userGameRepository, UserGameCustomRepository userGameCustomRepository, UserGameListRepository userGameListRepository) {
        this.igdbService = igdbService;
        this.userGameRepository = userGameRepository;
        this.userGameCustomRepository = userGameCustomRepository;
        this.userGameListRepository = userGameListRepository;
    }

    public CompletableFuture<List<EnrichedGameSearchDto>> searchGamesWithUserDataAsync(String query, User user) {
        return CompletableFuture.supplyAsync(() -> igdbService.searchGames(query))
                .thenCompose(searchResults -> {
                    if (searchResults == null || searchResults.isEmpty())  {
                        return CompletableFuture.completedFuture(List.of());
                    }

                    if (user == null) {
                        List<EnrichedGameSearchDto> result = searchResults.stream()
                                .map(EnrichedGameSearchDto::fromIgdb)
                                .toList();
                        return CompletableFuture.completedFuture(result);
                    }

                    Set<Long> gameIds = searchResults.stream()
                            .map(IgdbGameDto::id)
                            .collect(Collectors.toSet());

                    return CompletableFuture.supplyAsync(() -> loadUserGameDataBatch(user, gameIds))
                            .thenApply(userGameDataMap -> searchResults.stream()
                                    .map(igdbGame -> EnrichedGameSearchDto.fromIgdb(
                                            igdbGame,
                                            userGameDataMap.get(igdbGame.id())
                                    ))
                                    .toList());
                });
    }

    public List<EnrichedGameSearchDto> searchGamesWithUserData(String query, User user) {
        return searchGamesWithUserDataAsync(query, user).join();
    }

    public EnrichedGameDto getGameWithUserData(Long igdbId, User user) {
        IgdbGameDto igdbGame = igdbService.getGame(igdbId);

        if (user == null) {
            return EnrichedGameDto.fromIgdb(igdbGame);
        }

        UserGameData userGameDataMono = loadUserGameData(user, igdbId);

        return EnrichedGameDto.fromIgdb(igdbGame, userGameDataMono);
    }

    private Map<Long, UserGameBatchData> loadUserGameDataBatch(User user, Set<Long> igdbGameIds) {
        List<UserGameBatchData> baseData = userGameCustomRepository.getUserGamesBaseDataBatch(user.getUsername(), igdbGameIds);

        if (baseData.isEmpty()) {
            return Map.of();
        }

        Set<Long> userGameIds = baseData.stream()
                .map(UserGameBatchData::userGameId)
                .collect(Collectors.toSet());

        //Map<Long, List<GameListReference>> listsMap = loadGameListsBatch(userGameIds);

        return baseData.stream()
                .collect(Collectors.toMap(
                        UserGameBatchData::igdbId,
                        game -> game,
                        (existing, replacement) -> existing
                ));
    }

    private UserGameData loadUserGameData(User user, Long igdbGameId) {

        UserGameBaseData base = userGameCustomRepository
                .getUserGameBaseData(igdbGameId, user.getUsername())
                .orElse(null);

        // TODO Сделать список со всеми списками пользователя, но еще делать метки где игра уже добавлена

        if (base == null) {
            return null;
        }

        List<UserModeDto> modes = userGameCustomRepository.findUserModes(base.userGameId());

//        List<GameListReference> lists = loadGameLists(igdbGameId);
        return UserGameData.fromUserGameBase(base, modes);
    }

    private Map<Long, List<GameListReference>> loadGameListsBatch(Set<Long> userGameIds, User user) {
        List<Object[]> results = userGameListRepository
                .findListsByUserGameIdsAndAuthorUsername(userGameIds, user.getUsername());

        return results.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(
                                row -> new GameListReference(
                                        (UUID) row[1],
                                        (String) row[2],
                                        (Boolean) row[3]
                                ),
                                Collectors.toList()
                        )
                ));
    }

    private List<GameListReference> loadGameLists(Long userGameId, User user) {
        return userGameListRepository.findListsByUserGameIdAndAuthorUsername(userGameId, user.getUsername());
    }

    public EnrichedGameList getGameListWithUserData(UUID listId, User user) {
        UserGameList monoList =  userGameListRepository.findByIdWithItems(listId)
                .orElseThrow(() -> new EntityNotFoundException("Game list not found"));

        if (user == null) {
            return EnrichedGameList.fromUserGameList(monoList);
        }

        if (!monoList.isPublic()) {
            if (!monoList.isOwnedBy(user)) {
                throw new AccessDeniedException("You don't have permission to modify this list");
            }
        }

        Set<Long> igdbGameIdsMono = monoList.getItems().stream().map(item -> item.getGame().getIgdbId()).collect(Collectors.toSet());
        Map<Long, UserGameBatchData> games = loadUserGameDataBatch(user, igdbGameIdsMono);

        return EnrichedGameList.fromUserGameList(monoList, games);
    }
}