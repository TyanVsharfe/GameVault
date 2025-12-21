package com.gamevault.controller;

import com.gamevault.db.model.UserGameList;
import com.gamevault.db.model.User;
import com.gamevault.dto.input.UserGameListForm;
import com.gamevault.dto.input.update.UserGameListUpdateForm;
import com.gamevault.dto.output.UserGameListOutput;
import com.gamevault.service.UserGameListService;
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
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users/game-lists")
public class UserGameListController {
    private final UserGameListService userGameListService;

    public UserGameListController(UserGameListService userGameListService) {
        this.userGameListService = userGameListService;
    }

    @GetMapping("/{list-id}")
    public ResponseEntity<UserGameListOutput> get(@PathVariable("list-id") UUID listId,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userGameListService.getGameListById(listId, user).toOutput(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserGameList>> getAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "50") int size,
                                                     @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserGameList> games = userGameListService.getUserLists(user, pageable);
        return ResponseEntity.ok().body(games);
    }

    @PostMapping
    public ResponseEntity<UserGameList> add(@Valid @RequestBody UserGameListForm userGameListForm,
                                            @AuthenticationPrincipal User user) {
        UserGameList created = userGameListService.createList(userGameListForm, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userGameId}")
                .buildAndExpand(created.getUuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/{list-id}/copy")
    public ResponseEntity<UserGameList> copyUserList(@PathVariable("list-id") UUID listId,
                                                      @AuthenticationPrincipal User user) {
        UserGameList created = userGameListService.copyGameList(listId, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userGameId}")
                .buildAndExpand(created.getUuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{list-id}")
    public ResponseEntity<UserGameListOutput> put(@PathVariable("list-id") UUID listId,
                                            @Valid @RequestBody UserGameListUpdateForm userGameListUpdateForm,
                                            @AuthenticationPrincipal User user) {
        UserGameList updated = userGameListService.updateGameList(listId, userGameListUpdateForm, user);
        return ResponseEntity.ok(updated.toOutput(user));
    }

    @DeleteMapping("/{list-id}")
    public ResponseEntity<Void> delete(@PathVariable("list-id") UUID listId,
                                       @AuthenticationPrincipal User user) {
        userGameListService.deleteGameList(listId, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{list-id}/games/{game-id}")
    public ResponseEntity<Void> deleteGameFromList(
            @PathVariable("list-id") UUID listId,
            @PathVariable("game-id") Long igdbId,
            @AuthenticationPrincipal User user
    ) {
        userGameListService.removeGameFromList(listId, igdbId, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{list-id}/games")
    public ResponseEntity<Void> deleteGamesFromList(
            @PathVariable("list-id") UUID listId,
            @RequestParam List<Long> ids,
            @AuthenticationPrincipal User user
    ) {
        userGameListService.removeGamesFromList(listId, ids, user);
        return ResponseEntity.noContent().build();
    }
}
