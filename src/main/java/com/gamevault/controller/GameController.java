package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.dto.output.enriched.EnrichedGameDto;
import com.gamevault.dto.output.enriched.EnrichedGameSearchDto;
import com.gamevault.service.enriched.EnrichedGameQueryService;
import com.gamevault.service.enriched.EnrichedGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/games")
public class GameController {
    private final EnrichedGameService enrichedGameService;
    private final EnrichedGameQueryService enrichedGameQueryService;

    public GameController(EnrichedGameService enrichedGameService, EnrichedGameQueryService enrichedGameQueryService) {
        this.enrichedGameService = enrichedGameService;
        this.enrichedGameQueryService = enrichedGameQueryService;
    }

    @GetMapping("/search")
    public List<EnrichedGameSearchDto> search(
            @RequestParam String query,
            @AuthenticationPrincipal User user) {
        return enrichedGameQueryService.searchGamesWithUserData(query, user);
    }

    @GetMapping("/{igdb-id}")
    public ResponseEntity<EnrichedGameDto> get(
            @PathVariable("igdb-id") Long igdbId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ofNullable(enrichedGameService.getGameWithUserData(igdbId, user));
    }
}
