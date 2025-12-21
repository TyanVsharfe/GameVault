package com.gamevault.dto.output;

import com.gamevault.db.model.UserGame;

public record UserReviewsDto(
        Long id,
        String username,
        String review,
        Double userRating
) {
    public UserReviewsDto(UserGame userGame) {
        this (
                userGame.getId(),
                userGame.getUser().getUsername(),
                userGame.getReview(),
                userGame.getUserRating()
        );
    }
}
