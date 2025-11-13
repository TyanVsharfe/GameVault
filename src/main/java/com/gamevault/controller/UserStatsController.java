package com.gamevault.controller;

import com.gamevault.db.model.User;
import com.gamevault.dto.output.UserStatsDTO;
import com.gamevault.service.UserStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/users")
public class UserStatsController {
    private final UserStatsService userStatsService;

    public UserStatsController(UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }

    @GetMapping("/{username}/stats")
    private ResponseEntity<UserStatsDTO> getUserStat(@PathVariable("username") String username,
                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userStatsService.getUserStats(username));
    }

    @GetMapping("/me/stats")
    private ResponseEntity<UserStatsDTO> getOwnUserStat(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(userStatsService.getUserStats(user.getUsername()));
    }
}
