package com.gamevault.form.update;

import com.gamevault.data_template.Enums;
import com.gamevault.db.model.Note;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.lang.Nullable;

public record UserGameUpdateForm(
        @Nullable @Enumerated(EnumType.STRING) Enums.status status,
        @Nullable Boolean isFullyCompleted,
        @Nullable @DecimalMin("0.0") @DecimalMax("100.0") Double userRating,
        @Nullable @Size(max = 10000) String review,
        @Nullable @Valid Note note
) {}

