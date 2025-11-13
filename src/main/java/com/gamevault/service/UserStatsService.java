package com.gamevault.service;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.UserGame;
import com.gamevault.db.repository.UserGameRepository;
import com.gamevault.dto.output.UserStatsDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserStatsService {

    private final UserGameRepository userGameRepository;

    public UserStatsService(UserGameRepository userGameRepository) {
        this.userGameRepository = userGameRepository;
    }

    public UserStatsDTO getUserStats(String username) {
        Iterable<UserGame> userGames = userGameRepository.findGamesByUser_Username(username);

        Map<Enums.Status, Long> gamesByStatus = Arrays.stream(Enums.Status.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> userGameRepository.countGamesByStatusAndUser_Username(status, username)
                ));

        long totalGames = userGameRepository.countGamesByUser_Username(username);

        Double averageRating = calculateAverageRating(userGames);
        if (averageRating != null ) averageRating = Math.round(averageRating * 10.0) / 10.0;

        long totalNotes = calculateTotalNotes(userGames);

        UserStatsDTO stats = new UserStatsDTO();
        stats.setUsername(username);
        stats.setTotalGames(totalGames);
        stats.setGamesByStatus(gamesByStatus);
        stats.setAverageRating(averageRating);
        stats.setTotalNotes(totalNotes);

        return stats;
    }

    private Double calculateAverageRating(Iterable<UserGame> userGames) {
        double sum = 0.0;
        int count = 0;
        for (UserGame userGame : userGames) {
            if (userGame.getUserRating() != null) {
                sum += userGame.getUserRating();
                count++;
            }
        }
        return count > 0 ? sum / count : null;
    }

    private long calculateTotalNotes(Iterable<UserGame> userGames) {
        long totalNotes = 0;
        for (UserGame userGame : userGames) {
            totalNotes += userGame.getNotes().size();
        }
        return totalNotes;
    }

    private long countGamesByUser_Username(String username) {
        Iterable<UserGame> userGames = userGameRepository.findGamesByUser_Username(username);
        long count = 0;
        for (UserGame ignored : userGames) {
            count++;
        }
        return count;
    }
}