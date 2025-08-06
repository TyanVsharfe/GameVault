package com.gamevault.controller;

import com.gamevault.db.model.UserGame;
import com.gamevault.db.model.User;
import com.gamevault.dto.UserReviewsDTO;
import com.gamevault.form.update.*;
import com.gamevault.service.UserGameService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users/games")
public class UserGameController {
    private final UserGameService userGameService;

    public UserGameController(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    @GetMapping("/{igdbId}")
    public ResponseEntity<UserGame> get(@PathVariable Long igdbId,
                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userGameService.getByIgdbId(igdbId, user));
    }

    @GetMapping("/{igdbId}/reviews")
    public ResponseEntity<List<UserReviewsDTO>> getUserReviews(@PathVariable Long igdbId) {
        return ResponseEntity.ok().body(userGameService.getGameReviews(igdbId));
    }

    @GetMapping
    public ResponseEntity<Iterable<UserGame>> getAll(@RequestParam(value = "status", required = false) String status,
                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userGameService.getAll(status, user));
    }

    @PostMapping("/{igdbId}")
    public ResponseEntity<UserGame> add(@PathVariable Long igdbId,
                                        @AuthenticationPrincipal User user) {
        UserGame created = userGameService.add(igdbId, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userGameId}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{igdbId}")
    public ResponseEntity<Void> delete(@PathVariable Long igdbId,
                                       @AuthenticationPrincipal User user) {
        userGameService.delete(igdbId, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{igdbId}")
    public ResponseEntity<UserGame> put(@PathVariable Long igdbId,
                                        @Valid @RequestBody UserGameUpdateForm userGameUpdateForm,
                                        @AuthenticationPrincipal User user) {
        UserGame updated = userGameService.update(igdbId, user, userGameUpdateForm);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{igdbId}/status")
    public ResponseEntity<UserGame> updateStatus(@PathVariable Long igdbId,
                                                 @Valid @RequestBody StatusUpdateForm dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateStatus(igdbId, user, dto.status()));
    }

    @PatchMapping("/{igdbId}/fully-completed")
    public ResponseEntity<UserGame> updateFullyCompleted(@PathVariable Long igdbId,
                                                         @Valid @RequestBody FullyCompletedUpdateForm dto,
                                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateFullyCompleted(igdbId, user, dto.fullyCompleted()));
    }

    @PatchMapping("/{igdbId}/rating")
    public ResponseEntity<UserGame> updateRating(@PathVariable Long igdbId,
                                                 @Valid @RequestBody UserRatingUpdateForm dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateRating(igdbId, user, dto.userRating()));
    }

    @PatchMapping("/{igdbId}/review")
    public ResponseEntity<UserGame> updateReview(@PathVariable Long igdbId,
                                                 @Valid @RequestBody ReviewUpdateForm dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateReview(igdbId, user, dto.review()));
    }

    @GetMapping("/exists/{igdbId}")
    public ResponseEntity<Void> isContains(@PathVariable Long igdbId,
                                           @AuthenticationPrincipal User user) {
        if (userGameService.isContains(igdbId, user)) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
}
