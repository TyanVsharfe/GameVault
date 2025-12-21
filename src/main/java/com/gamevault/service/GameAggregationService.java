package com.gamevault.service;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGameList;
import com.gamevault.db.repository.UserGameListRepository;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.dto.output.db.UserGameBaseData;
import com.gamevault.dto.output.db.UserGameBatchData;
import com.gamevault.dto.output.enriched.*;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.exception.GameNotFoundInIgdbException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameAggregationService {

    private final IgdbGameService igdbService;
    private final UserGameRepository userGameRepository;
    private final UserGameListRepository userGameListRepository;

    public GameAggregationService(IgdbGameService igdbService, UserGameRepository userGameRepository, UserGameListRepository userGameListRepository) {
        this.igdbService = igdbService;
        this.userGameRepository = userGameRepository;
        this.userGameListRepository = userGameListRepository;
    }

    public Mono<List<EnrichedGameSearchDto>> searchGamesWithUserData(String query, User user) {
        List<IgdbGameDto> searchResults = igdbService.searchGames(query).block(Duration.ofSeconds(10));

        if (searchResults == null || searchResults.isEmpty())  {
            return Mono.just(List.of());
        }

        if (user == null) {
            List<EnrichedGameSearchDto> result = searchResults.stream()
                    .map(EnrichedGameSearchDto::fromIgdb)
                    .toList();
            return Mono.just(result);
        }

        Set<Long> gameIds = searchResults.stream()
                .map(IgdbGameDto::id)
                .collect(Collectors.toSet());

        return loadUserGameDataBatch(user, gameIds)
                .map(userGameDataMap -> searchResults.stream()
                        .map(igdbGame -> EnrichedGameSearchDto.fromIgdb(
                                igdbGame,
                                userGameDataMap.get(igdbGame.id())
                        ))
                        .toList()
                );
    }

    public Mono<EnrichedGameDto> getGameWithUserData(Long igdbId, User user) {
        Mono<IgdbGameDto> igdbGameMono = igdbService.getGame(igdbId).switchIfEmpty(
                Mono.error(new GameNotFoundInIgdbException("Game with id " + igdbId + " not found in IGDB.")));

        if (user == null) {
            return igdbGameMono
                    .map(EnrichedGameDto::fromIgdb);
        }

        Mono<UserGameData> userGameDataMono = loadUserGameData(user, igdbId);

        return Mono.zip(igdbGameMono, userGameDataMono)
                .map(tuple -> EnrichedGameDto.fromIgdb(tuple.getT1(), tuple.getT2()));
    }

    private Mono<Map<Long, UserGameBatchData>> loadUserGameDataBatch(User user, Set<Long> igdbGameIds) {
        Mono<List<UserGameBatchData>> baseMono =
                Mono.fromCallable(() ->
                        userGameRepository
                                .getUserGamesBaseDataByUsername(user.getUsername(), igdbGameIds)
                ).subscribeOn(Schedulers.boundedElastic());


        return baseMono.flatMap(base -> {
            if (base.isEmpty()) {
                return Mono.just(Map.of());
            }

            Set<Long> userGameIds = base.stream()
                    .map(UserGameBatchData::userGameId)
                    .collect(Collectors.toSet());

            //Map<Long, List<GameListReference>> listsMap = loadGameListsBatch(userGameIds);

            return Flux.fromIterable(base)
                    .flatMap(game ->
                            Mono.fromCallable(() -> userGameRepository.findUserModes(game.userGameId()))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .map(modes -> Map.entry(
                                            game.igdbId(),
                                            new UserGameBatchData(
                                                    game.userGameId(),
                                                    game.igdbId(),
                                                    game.status(),
                                                    game.userRating(),
                                                    game.review(),
                                                    game.isFullyCompleted(),
                                                    game.isOverallRatingManual(),
                                                    game.isOverallStatus(),
                                                    game.userCoverUrl(),
                                                    game.createdAt(),
                                                    game.updatedAt(),
                                                    game.notesCount()
                                            )
                                    ))
                    )
                    .collectMap(Map.Entry::getKey, Map.Entry::getValue);
        });
    }

    private Mono<UserGameData> loadUserGameData(User user, Long igdbGameId) {

        Mono<UserGameBaseData> baseMono =
                Mono.fromCallable(() ->
                        userGameRepository
                                .getUserGameBaseDataByUsername(igdbGameId, user.getUsername())
                                .orElse(null)
                ).subscribeOn(Schedulers.boundedElastic());

        // TODO Сделать список со всеми списками пользователя, но еще делать метки где игра уже добавлена
//        List<GameListReference> lists = loadGameLists(igdbGameId);
        return baseMono.flatMap(base -> {
            if (base == null) {
                return Mono.justOrEmpty((UserGameData) null);
            }

            return Mono.fromCallable(() ->
                            userGameRepository.findUserModes(base.userGameId())
                    )
                    .subscribeOn(Schedulers.boundedElastic())
                    .map(modes -> new UserGameData(
                            base.userGameId(),
                            base.status(),
                            base.userRating(),
                            base.review(),
                            base.isFullyCompleted(),
                            base.isOverallRatingManual(),
                            base.isOverallStatus(),
                            base.userCoverUrl(),
                            base.createdAt(),
                            base.updatedAt(),
                            base.notesCount(),
                            modes,
                            null,
                            null,
                            null
                    ));
        });
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

    public Mono<EnrichedGameList> getGameListWithUserData(UUID listId, User user) {
        Mono<UserGameList> monoList =
                Mono.fromCallable(() ->
                        userGameListRepository.findByIdWithItems(listId)
                                .orElseThrow(() -> new EntityNotFoundException("Game list not found"))
                ).subscribeOn(Schedulers.boundedElastic());

        if (user == null) {
            return monoList.map(EnrichedGameList::fromUserGameList);
        }

        Mono<Set<Long>> igdbGameIdsMono = monoList.handle((list, sink) -> {
            if (!list.isPublic()) {
                if (!list.isOwnedBy(user)) {
                    sink.error(new AccessDeniedException("You don't have permission to modify this list"));
                    return;
                }
            }
            sink.next(list.getItems().stream().map(item -> item.getGame().getIgdbId()).collect(Collectors.toSet()));
        });

        return igdbGameIdsMono
                .flatMap(igdbId -> loadUserGameDataBatch(user, igdbId))
                .flatMap(userGameDataMap -> monoList
                        .map(userGameList -> EnrichedGameList.fromUserGameList(
                                userGameList,
                                userGameDataMap)
                        ));
    }
}