package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.dto.input.SteamImportTask;
import com.gamevault.dto.output.enriched.EnrichedGameSearchDto;
import com.gamevault.service.enriched.SteamImportPreviewService;
import com.gamevault.service.integration.steam.SteamImportProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/steam-import")
public class SteamImportController {
    private final SteamImportPreviewService steamImportPreviewService;
    private final SteamImportProducer steamImportProducer;

    public SteamImportController(SteamImportPreviewService steamImportPreviewService, SteamImportProducer steamImportProducer) {
        this.steamImportPreviewService = steamImportPreviewService;
        this.steamImportProducer = steamImportProducer;
    }

    @PostMapping("/{steam-id}")
    public ResponseEntity<String> importSteamGames(@PathVariable("steam-id") Long steamId, @RequestBody List<Long> selectedGames,
                                                   @AuthenticationPrincipal User user) {
        SteamImportTask steamImportTask = new SteamImportTask(user.getId(), steamId, selectedGames);
        steamImportProducer.sendSteamImportTask(steamImportTask);
        return ResponseEntity.ok("The import task has been sent");
    }

    @GetMapping("/{steam-id}")
    public ResponseEntity<List<EnrichedGameSearchDto>> getSteamGames(@PathVariable("steam-id") Long steamId,
                                                                     @AuthenticationPrincipal User user) {
        List<EnrichedGameSearchDto> games = steamImportPreviewService.importSteamGamesWithUserData(steamId, user);
        return ResponseEntity.ok(games);
    }
}
