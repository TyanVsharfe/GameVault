package com.gamevault.dto.input.update;

import com.gamevault.enums.Enums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.lang.Nullable;

public record UserGameModeUpdateForm(
        @Nullable @Enumerated(EnumType.STRING) Enums.Status status,
        @Nullable Boolean isOverallRatingManual,
        @Nullable @DecimalMin("0.0") @DecimalMax("100.0") Double userRating
) {}
