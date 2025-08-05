package com.gamevault.form.update;

import com.gamevault.data_template.Enums;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateForm(
        @NotNull @Enumerated(EnumType.STRING) Enums.status status
) {}
