package com.gamevault.service.filter;

import com.gamevault.dto.input.UserGamesFilterParams;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGameFilterFactory {
    public List<UserGameFilterSpecification> createFilters(UserGamesFilterParams params, String username) {
        return List.of(
                UserGameFilterSpecifications.usernameEquals(username),
                UserGameFilterSpecifications.statusEquals(params.getStatus()),
                UserGameFilterSpecifications.ratingBetween(params.getMinRating(), params.getMaxRating()),
                UserGameFilterSpecifications.hasReview(params.getHasReview()),
                UserGameFilterSpecifications.isFullyCompleted(params.getIsFullyCompleted()),
                UserGameFilterSpecifications.gameTypeEquals(params.getGameType()),
                UserGameFilterSpecifications.titleContains(params.getTitle()),
                UserGameFilterSpecifications.createdBetween(params.getCreatedAfter(), params.getCreatedBefore())
        );
    }
}
