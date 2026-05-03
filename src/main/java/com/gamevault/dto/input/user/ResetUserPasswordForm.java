package com.gamevault.dto.input.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetUserPasswordForm(
        @NotNull @Size(max = 254) String email
) {}
