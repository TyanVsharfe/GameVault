package com.gamevault.dto.input;

import com.gamevault.enums.Enums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record NoteForm(
        @NotNull @NotBlank @Size(max = 100) String title,
        @NotNull @NotBlank @Size(max = 10000) String content,
        @Nullable @Enumerated(EnumType.STRING) Enums.NoteType type
) { }