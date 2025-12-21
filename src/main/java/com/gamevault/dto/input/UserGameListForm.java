package com.gamevault.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

import java.util.List;

public record UserGameListForm(
        @NotNull(message = "Name cannot be null")
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        String name,
        @Nullable String description,
        @Nullable Boolean isPublic,
        @Nullable List<Long> games
)
{}
