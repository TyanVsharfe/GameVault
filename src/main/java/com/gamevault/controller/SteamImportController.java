package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.dto.input.SteamImportTask;
import com.gamevault.dto.output.igdb.IgdbGameDto;
import com.gamevault.service.integration.steam.SteamImportProducer;
import com.gamevault.service.integration.steam.SteamImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/steam-import")
public class SteamImportController {
    private final SteamImportService steamImportService;
    private final SteamImportProducer steamImportProducer;

    public SteamImportController(SteamImportService steamImportService, SteamImportProducer steamImportProducer) {
        this.steamImportService = steamImportService;
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
    public ResponseEntity<List<IgdbGameDto>> getSteamGames(@PathVariable("steam-id") Long steamId,
                                                                 @AuthenticationPrincipal User user) {
        List<IgdbGameDto> games = steamImportService.importSteamGames(steamId, user);
        return ResponseEntity.ok(games);
    }
}
