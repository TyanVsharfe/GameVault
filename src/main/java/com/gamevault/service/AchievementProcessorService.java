package com.gamevault.service;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.repository.UserGameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AchievementProcessorService {
    private final UserGameRepository userGameRepository;
    private final AchievementService achievementService;

    public AchievementProcessorService(UserGameRepository userGameRepository, AchievementService achievementService) {
        this.userGameRepository = userGameRepository;
        this.achievementService = achievementService;
    }

    @Transactional(readOnly = true)
    public void checkTotalGamesCompleted(User user) {
        long totalCompleted = userGameRepository.countGamesByStatusAndUser_Id(Enums.status.Completed, user.getId());

        Iterable<Achievement> totalGamesAchievements = achievementService.getAchievementsByCategory(Enums.AchievementCategory.TOTAL_GAMES_COMPLETED);

        for (Achievement achievement : totalGamesAchievements) {
            achievementService.updateAchievementProgress(user.getId(), achievement.getId(), (int) totalCompleted);
        }
    }

    @Transactional
    public void processBookCompletion(User user, UserGame userGame) {
        checkTotalGamesCompleted(user);
    }
}
