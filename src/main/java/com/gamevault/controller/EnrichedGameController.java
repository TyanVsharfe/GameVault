package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.dto.output.enriched.EnrichedGameDto;
import com.gamevault.dto.output.enriched.EnrichedGameList;
import com.gamevault.dto.output.enriched.EnrichedGameSearchDto;
import com.gamevault.service.GameAggregationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/games/enriched")
public class EnrichedGameController {

    private final GameAggregationService gameAggregationService;

    public EnrichedGameController(GameAggregationService gameAggregationService) {
        this.gameAggregationService = gameAggregationService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<EnrichedGameSearchDto>> searchGames(
            @RequestParam String query,
            @AuthenticationPrincipal User user) {
        List<EnrichedGameSearchDto> result = gameAggregationService.searchGamesWithUserData(query, user);

        if (result == null || result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/game-lists/{list-id}")
    public ResponseEntity<EnrichedGameList> getGameList(
            @PathVariable("list-id") UUID listId,
            @AuthenticationPrincipal User user) {
        try {
            EnrichedGameList gameList = gameAggregationService.getGameListWithUserData(listId, user);
            return ResponseEntity.ok(gameList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{igdbId}")
    public ResponseEntity<EnrichedGameDto> getGame(
            @PathVariable Long igdbId,
            @AuthenticationPrincipal User user) {
        EnrichedGameDto game = gameAggregationService.getGameWithUserData(igdbId, user);

        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(game);
    }
}
