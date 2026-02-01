package com.gamevault.service.achievement;

import com.gamevault.db.model.achievement.SeriesAchievement;
import com.gamevault.db.model.achievement.SeriesPart;
import com.gamevault.enums.Enums;
import com.gamevault.db.model.achievement.Achievement;
import com.gamevault.db.model.User;
import com.gamevault.db.repository.UserGameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class AchievementProcessorService {
    private final UserGameRepository userGameRepository;
    private final AchievementService achievementService;

    public AchievementProcessorService(UserGameRepository userGameRepository, AchievementService achievementService) {
        this.userGameRepository = userGameRepository;
        this.achievementService = achievementService;
    }

    private void checkTotalGamesCompleted(User user) {
        long totalCompleted = userGameRepository.countGamesByStatusAndUser_Id(Enums.Status.COMPLETED, user.getId());

        Iterable<Achievement> totalGamesAchievements =
                achievementService.getAchievementsByCategory(Enums.AchievementCategory.TOTAL_GAMES_COMPLETED);

        for (Achievement achievement : totalGamesAchievements) {
            achievementService.updateAchievementProgress(user.getId(), achievement.getId(), (int) totalCompleted);
        }
    }

    private void checkSeriesAchievement(User user) {
        Set<Long> completedGameIds = userGameRepository.findCompletedGameIdsByUserId(user.getId());

        Iterable<Achievement> totalGamesAchievements =
                achievementService.getAchievementsByCategory(Enums.AchievementCategory.SERIES_COMPLETED);

        for (Achievement achievement : totalGamesAchievements) {
            if (achievement instanceof SeriesAchievement seriesAchievement) {
                List<SeriesPart> requiredGameIds = seriesAchievement.getRequiredGames();
                long seriesProgress = requiredGameIds.stream()
                        .filter(part -> part.getGameIds()
                                .stream().anyMatch(completedGameIds::contains)).count();
                achievementService.updateAchievementProgress(user.getId(), seriesAchievement.getId(), (int) seriesProgress);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAchievementCompletion(User user) {
        checkTotalGamesCompleted(user);
        checkSeriesAchievement(user);
    }
}
