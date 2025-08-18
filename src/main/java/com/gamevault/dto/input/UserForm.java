package com.gamevault.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserForm(
        @NotNull @Size(max = 20) String username,
        @NotNull @Size(max = 30) String password) {
}
