package com.gamevault.form.update;

import jakarta.validation.constraints.NotNull;

public record FullyCompletedUpdateForm(
        @NotNull Boolean fullyCompleted
) {}
