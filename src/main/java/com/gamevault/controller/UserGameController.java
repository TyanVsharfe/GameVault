package com.gamevault.controller;

import com.gamevault.db.model.UserGame;
import com.gamevault.db.model.User;
import com.gamevault.dto.input.update.*;
import com.gamevault.dto.output.UserReviewsDTO;
import com.gamevault.enums.Enums;
import com.gamevault.service.UserGameService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/{igdb-id}")
    public ResponseEntity<UserGame> get(@PathVariable("igdb-id") Long igdbId,
                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userGameService.getByIgdbId(igdbId, user));
    }

    @GetMapping("/{igdb-id}/reviews")
    public ResponseEntity<List<UserReviewsDTO>> getUserReviews(@PathVariable("igdb-id") Long igdbId) {
        return ResponseEntity.ok().body(userGameService.getGameReviews(igdbId));
    }

    @GetMapping
    public ResponseEntity<Page<UserGame>> getAll(@RequestParam(value = "status", required = false) String status,
                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "50") int size,
                                                     @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserGame> games = userGameService.getAll(status, user, pageable);
        return ResponseEntity.ok().body(games);
    }

    @PostMapping("/{igdb-id}")
    public ResponseEntity<UserGame> add(@PathVariable("igdb-id") Long igdbId,
                                        @AuthenticationPrincipal User user) {
        UserGame created = userGameService.add(igdbId, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userGameId}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{igdb-id}")
    public ResponseEntity<Void> delete(@PathVariable("igdb-id") Long igdbId,
                                       @AuthenticationPrincipal User user) {
        userGameService.delete(igdbId, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{igdb-id}")
    public ResponseEntity<UserGame> put(@PathVariable("igdb-id") Long igdbId,
                                        @Valid @RequestBody UserGameUpdateForm userGameUpdateForm,
                                        @AuthenticationPrincipal User user) {
        UserGame updated = userGameService.update(igdbId, user, userGameUpdateForm);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{igdb-id}/status")
    public ResponseEntity<UserGame> updateStatus(@PathVariable("igdb-id") Long igdbId,
                                                 @Valid @RequestBody StatusUpdateForm dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateStatus(igdbId, user, dto.status()));
    }

    @PatchMapping("/{igdb-id}/fully-completed")
    public ResponseEntity<UserGame> updateFullyCompleted(@PathVariable("igdb-id") Long igdbId,
                                                         @Valid @RequestBody FullyCompletedUpdateForm dto,
                                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateFullyCompleted(igdbId, user, dto.fullyCompleted()));
    }

    @PutMapping("/{igdb-id}/modes/{mode}")
    public ResponseEntity<UserGame> updateMode(@PathVariable("igdb-id") Long igdbId,
                                               @PathVariable("mode") String modeString,
                                               @Valid @RequestBody UserGameModeUpdateForm dto,
                                               @AuthenticationPrincipal User user) {
        Enums.GameModesIGDB mode = Enums.GameModesIGDB.fromJson(modeString);
        return ResponseEntity.ok(userGameService.updateMode(igdbId, user, mode, dto));
    }

    @PatchMapping("/{igdb-id}/modes/{mode}/rating")
    public ResponseEntity<UserGame> updateModeRating(@PathVariable("igdb-id") Long igdbId,
                                                     @PathVariable Enums.GameModesIGDB mode,
                                                     @Valid @RequestBody UserRatingUpdateForm dto,
                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateModeRating(igdbId, user, mode, dto.userRating()));
    }

    @PatchMapping("/{igdb-id}/rating")
    public ResponseEntity<UserGame> updateRating(@PathVariable("igdb-id") Long igdbId,
                                                 @Valid @RequestBody UserRatingUpdateForm dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateOverallRating(igdbId, user, dto.userRating()));
    }

    @PatchMapping("/{igdb-id}/review")
    public ResponseEntity<UserGame> updateReview(@PathVariable("igdb-id") Long igdbId,
                                                 @Valid @RequestBody ReviewUpdateForm dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userGameService.updateReview(igdbId, user, dto.review()));
    }

    @GetMapping("/exists/{igdb-id}")
    public ResponseEntity<Void> isContains(@PathVariable("igdb-id") Long igdbId,
                                           @AuthenticationPrincipal User user) {
        if (userGameService.isContains(igdbId, user)) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
}
