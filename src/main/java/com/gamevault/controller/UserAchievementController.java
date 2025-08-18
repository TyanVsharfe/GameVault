package com.gamevault.controller;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.dto.output.achievement.UserAchievementDTO;
import com.gamevault.service.achievement.AchievementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

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
    public ResponseEntity<Iterable<UserAchievementDTO>> getUserAchievements(HttpServletRequest request,
                                                                            @AuthenticationPrincipal User user) {
        Locale locale = request.getLocale();
        String lang = locale.getLanguage();
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId(), lang));
    }

    @PostMapping("")
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement achievement) {
        return ResponseEntity.ok(achievementService.createAchievement(achievement));
    }
}
