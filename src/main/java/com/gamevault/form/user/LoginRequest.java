package com.gamevault.form.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull @Size(max = 20) String username,
        @NotNull @Size(max = 30) String password,
        boolean remember_me
) {}