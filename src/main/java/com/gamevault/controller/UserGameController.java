package com.gamevault.controller;

import com.gamevault.db.model.UserGame;
import com.gamevault.db.model.User;
import com.gamevault.form.UserGameUpdateDTO;
import com.gamevault.form.UserReviewsDTO;
import com.gamevault.service.UserGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("users/games")
public class UserGameController {
    private final UserGameService userGameService;

    public UserGameController(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGame> get(@PathVariable("id") Long igdbId, @AuthenticationPrincipal User user) {
        return userGameService.getByIgdbId(igdbId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<UserReviewsDTO>> getUserReviews(@PathVariable("id") Long igdbId) {
        return userGameService.getGameReviews(igdbId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UserGame>> getAll(@RequestParam(value = "status", required = false) String status, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userGameService.getAll(status, user));
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserGame> add(@PathVariable("id") Long igdbId, @AuthenticationPrincipal User user) {
        UserGame created = userGameService.add(igdbId, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userGameId}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long igdbId, @AuthenticationPrincipal User user) {
        userGameService.delete(igdbId, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGame> put(@PathVariable("id") Long id, @RequestBody UserGameUpdateDTO userGameUpdateDTO, @AuthenticationPrincipal User user) {
        UserGame updated = userGameService.update(id, user, userGameUpdateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/check-entity/{id}")
    public ResponseEntity<Void> isContains(@PathVariable("id") Long igdbId, @AuthenticationPrincipal User user) {
        if (userGameService.isContains(igdbId, user)) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
}
