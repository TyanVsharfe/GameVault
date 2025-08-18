package com.gamevault.dto.input.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteUpdateForm(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 10000) String content
) {}
