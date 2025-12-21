package com.gamevault.controller;

import com.gamevault.dto.input.achievement.AchievementForm;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.achievement.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.dto.output.achievement.UserAchievementDto;
import com.gamevault.service.achievement.AchievementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Iterable<UserAchievementDto>> getUserAchievements(HttpServletRequest request,
                                                                            @AuthenticationPrincipal User user) {
        Locale locale = request.getLocale();
        String lang = locale.getLanguage();
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId(), lang));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody AchievementForm achievement) {
        return ResponseEntity.ok(achievementService.createAchievement(achievement));
    }
}
