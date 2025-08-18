package com.gamevault.dto.input.update;

import jakarta.validation.constraints.NotNull;

public record FullyCompletedUpdateForm(
        @NotNull Boolean fullyCompleted
) {}
