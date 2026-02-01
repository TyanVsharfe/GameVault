package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.db.model.UserProfile;
import com.gamevault.dto.input.steam.UserSteamSettings;
import com.gamevault.dto.input.steam.UserSteamSync;
import com.gamevault.service.user.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/integrations/steam")
    public ResponseEntity<UserProfile> getUserSettings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.getUserSettings(user));
    }

    @PutMapping("/integrations/steam")
    public ResponseEntity<UserProfile> steamSettingsUpdate(@RequestBody UserSteamSettings steamSettings,
                                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.steamSettingsUpdate(steamSettings, user));
    }

    @DeleteMapping("/integrations/steam")
    public ResponseEntity<UserProfile> steamSettingsRemove(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.steamSettingsDelete(user));
    }

    @PostMapping("/integrations/steam/sync")
    public ResponseEntity<UserSteamSync> steamSyncStart(@RequestBody UserSteamSync userSteamSync,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.steamSyncStart(userSteamSync, user));
    }

    @DeleteMapping("/integrations/steam/sync")
    public ResponseEntity<UserSteamSettings> steamSyncRemove(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.steamSyncRemove(user));
    }

    @GetMapping("/integrations/steam/ignored-games")
    public ResponseEntity<String> getIgnoredSteamGames() {
        return ResponseEntity.ok("");
    }

    @PostMapping("/integrations/steam/ignored-games/{gameId}")
    public ResponseEntity<String> addIgnoredSteamGame(@PathVariable String gameId) {
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/integrations/steam/ignored-games/{gameId}")
    public ResponseEntity<String> deleteIgnoredSteamGame(@PathVariable String gameId) {
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/integrations/steam/ignored-games")
    public ResponseEntity<String> deleteIgnoredSteamGames(@PathVariable Set<Long> gameId) {
        return ResponseEntity.ok("");
    }
}
