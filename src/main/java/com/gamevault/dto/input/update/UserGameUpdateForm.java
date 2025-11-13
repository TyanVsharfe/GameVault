package com.gamevault.dto.input.update;

import com.gamevault.enums.Enums;
import com.gamevault.db.model.Note;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.lang.Nullable;

public record UserGameUpdateForm(
        @Nullable @Enumerated(EnumType.STRING) Enums.Status status,
        @Nullable Boolean isFullyCompleted,
        @Nullable @DecimalMin("0.0") @DecimalMax("100.0") Double userRating,
        @Nullable Boolean resetUserRating,
        @Nullable @Size(max = 10000) String review,
        @Nullable @Valid Note note
) {}

