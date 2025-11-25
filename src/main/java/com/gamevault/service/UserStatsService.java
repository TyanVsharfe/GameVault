package com.gamevault.service;

import com.gamevault.enums.Enums;
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
        Map<Enums.Status, Long> gamesByStatus = Arrays.stream(Enums.Status.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> userGameRepository.countGamesByStatusAndUser_Username(status, username)
                ));

        long totalGames = userGameRepository.countGamesByUser_Username(username);

        Double averageRating = userGameRepository.calculateAverageRatingByUsername(username);
        if (averageRating != null ) {
            averageRating = Math.round(averageRating * 10.0) / 10.0;
        }

        long totalNotes = userGameRepository.countNotesByUsername(username);

        UserStatsDTO stats = new UserStatsDTO();
        stats.setUsername(username);
        stats.setTotalGames(totalGames);
        stats.setGamesByStatus(gamesByStatus);
        stats.setAverageRating(averageRating);
        stats.setTotalNotes(totalNotes);

        return stats;
    }
}