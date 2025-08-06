package com.gamevault.controller;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserAchievement;
import com.gamevault.service.AchievementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users/achievements")
public class UserAchievementController {
    private final AchievementService achievementService;

    public UserAchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Iterable<Achievement>> getAchievementsByCategory(@PathVariable Enums.AchievementCategory category) {
        return ResponseEntity.ok(achievementService.getAchievementsByCategory(category));
    }

    @GetMapping
    public ResponseEntity<Iterable<UserAchievement>> getUserAchievements(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId()));
    }

    @PostMapping("")
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement achievement) {
        return ResponseEntity.ok(achievementService.createAchievement(achievement));
    }
}
