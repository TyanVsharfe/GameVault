package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.repository.UserGameCustomRepository;
import com.gamevault.db.repository.UserGameListRepository;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.*;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.IgdbGameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GameAggregationService {

    private final IgdbGameService igdbService;
    private final UserGameCustomRepository userGameCustomRepository;
    private final UserGameListRepository userGameListRepository;

    public GameAggregationService(IgdbGameService igdbService, UserGameCustomRepository userGameCustomRepository, UserGameListRepository userGameListRepository) {
        this.igdbService = igdbService;
        this.userGameCustomRepository = userGameCustomRepository;
        this.userGameListRepository = userGameListRepository;
    }

    public List<EnrichedGameSearchDto> searchGamesWithUserData(String query, User user) {
        return searchGamesWithUserDataAsync(query, user).join();
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

    private Map<Long, UserGameBatchData> loadUserGameDataBatch(User user, Set<Long> igdbIds) {
        List<UserGameBatchData> baseData = userGameCustomRepository.getUserGamesBaseDataBatch(user.getUsername(), igdbIds);

        if (baseData.isEmpty()) {
            return Map.of();
        }

        Map<Long, UserGameBatchData> gamesMap = baseData.stream()
                .collect(Collectors.toMap(
                        UserGameBatchData::getIgdbId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<Long, List<GameListReference>> listsMap = userGameCustomRepository.getGameListsMap(user.getUsername(), igdbIds);

        listsMap.forEach((igdbId, references) -> {
            UserGameBatchData data = gamesMap.get(igdbId);
            if (data != null) {
                data.setInLists(references);
            }
        });

        return gamesMap;
    }

    public EnrichedGameDto getGameWithUserData(Long igdbId, User user) {
        IgdbGameDto igdbGame = igdbService.getGame(igdbId);

        if (user == null) {
            return EnrichedGameDto.fromIgdb(igdbGame);
        }

        UserGameData userGameDataMono = loadUserGameData(user, igdbId);

        return EnrichedGameDto.fromIgdb(igdbGame, userGameDataMono);
    }

    private UserGameData loadUserGameData(User user, Long igdbGameId) {

        UserGameBaseData base = userGameCustomRepository
                .getUserGameBaseData(igdbGameId, user.getUsername())
                .orElse(null);

        if (base == null) {
            return null;
        }

        List<UserModeDto> modes = userGameCustomRepository.findUserModes(base.userGameId());

        List<GameListReference> lists = loadGameLists(igdbGameId, user);
        return UserGameData.fromUserGameBase(base, modes, lists);
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