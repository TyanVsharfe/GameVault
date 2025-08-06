package com.gamevault.dto;

import com.gamevault.db.model.UserGame;

public record UserReviewsDTO(
        Long id,
        String username,
        String review,
        Double userRating
) {
    public UserReviewsDTO(UserGame userGame) {
        this (
                userGame.getId(),
                userGame.getUser().getUsername(),
                userGame.getReview(),
                userGame.getUserRating()
        );
    }
}
