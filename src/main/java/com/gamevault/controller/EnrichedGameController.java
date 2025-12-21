package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.dto.output.enriched.EnrichedGameDto;
import com.gamevault.dto.output.enriched.EnrichedGameList;
import com.gamevault.dto.output.enriched.EnrichedGameSearchDto;
import com.gamevault.service.GameAggregationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/games/enriched")
public class EnrichedGameController {

    private final GameAggregationService gameAggregationService;

    // TODO агрегация UserGameController и IgdbGameApi сюда на url "${api.prefix}/games"
    // TODO сделать агрегацию с UserGameList UserGameModes и DLC
    public EnrichedGameController(GameAggregationService gameAggregationService) {
        this.gameAggregationService = gameAggregationService;
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<List<EnrichedGameSearchDto>>> searchGames(
            @RequestParam String query,
            @AuthenticationPrincipal User user) {

        return gameAggregationService
                .searchGamesWithUserData(query, user)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    // TODO Сделать агрегацию списков
    @GetMapping("/game-lists/{list-id}")
    public Mono<ResponseEntity<EnrichedGameList>> getGameList(
            @PathVariable("list-id") UUID listId,
            @AuthenticationPrincipal User user) {

        return gameAggregationService
                .getGameListWithUserData(listId, user)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{igdbId}")
    public Mono<ResponseEntity<EnrichedGameDto>> getGame(
            @PathVariable Long igdbId,
            @AuthenticationPrincipal User user) {

        return gameAggregationService
                .getGameWithUserData(igdbId, user)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
