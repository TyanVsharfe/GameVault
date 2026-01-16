package com.gamevault.service;

import com.gamevault.db.repository.UserGameCustomRepository;
import com.gamevault.db.repository.achievement.UserAchievementRepository;
import com.gamevault.enums.Enums;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.dto.output.UserStatsDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserStatsService {

    private final UserGameRepository userGameRepository;
    private final UserGameCustomRepository userGameCustomRepository;
    private final UserAchievementRepository userAchievementRepository;

    public UserStatsService(UserGameRepository userGameRepository, UserGameCustomRepository userGameCustomRepository, UserAchievementRepository userAchievementRepository) {
        this.userGameRepository = userGameRepository;
        this.userGameCustomRepository = userGameCustomRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    public UserStatsDto getUserStats(String username) {
        Map<Enums.Status, Long> gamesByStatus = Arrays.stream(Enums.Status.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> userGameRepository.countGamesByStatusAndUser_Username(status, username)
                ));

        long totalGames = userGameRepository.countGamesByUser_Username(username);
        long totalAchievementsCompleted = userAchievementRepository.countCompletedAchievements(username);

        Double averageRating = userGameCustomRepository.calculateAverageRating(username);
        if (averageRating != null ) {
            averageRating = Math.round(averageRating * 10.0) / 10.0;
        }

        long totalNotes = userGameRepository.countNotesByUsername(username);

        UserStatsDto stats = new UserStatsDto();
        stats.setUsername(username);
        stats.setTotalGames(totalGames);
        stats.setTotalCompletedAchievements(totalAchievementsCompleted);
        stats.setGamesByStatus(gamesByStatus);
        stats.setAverageRating(averageRating);
        stats.setTotalNotes(totalNotes);

        return stats;
    }
}