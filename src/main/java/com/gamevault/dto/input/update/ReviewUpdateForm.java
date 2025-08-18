package com.gamevault.dto.input.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewUpdateForm(
        @NotBlank @Size(max = 10000) String review
) {}
