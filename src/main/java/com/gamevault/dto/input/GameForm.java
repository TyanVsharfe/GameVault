package com.gamevault.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GameForm(
        @NotNull @Min(1) Long igdbId,
        @NotNull @Size(max = 1000) String title,
        @NotNull @Size(max = 2000) String coverUrl,
        @NotNull @Size(max = 10000) String description
) {}

