package com.gamevault.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteForm(
        @NotNull @NotBlank @Size(max = 100) String title,
        @NotNull @NotBlank @Size(max = 10000) String content
) { }