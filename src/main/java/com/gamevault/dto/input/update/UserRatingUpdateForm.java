package com.gamevault.dto.input.update;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UserRatingUpdateForm(
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double userRating
) {}
