package com.gamevault.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record UserFormLogin(
        @NotNull @Size(max = 20) String username,
        @NotNull @Size(max = 30) String password,
        @JsonProperty("remember-me")
        @Nullable String rememberMe) {
}
