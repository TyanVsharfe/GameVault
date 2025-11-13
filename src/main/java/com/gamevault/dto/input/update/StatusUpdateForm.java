package com.gamevault.dto.input.update;

import com.gamevault.enums.Enums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateForm(
        @NotNull @Enumerated(EnumType.STRING) Enums.Status status
) {}
