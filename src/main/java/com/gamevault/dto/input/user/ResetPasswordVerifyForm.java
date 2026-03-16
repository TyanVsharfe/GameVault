package com.gamevault.dto.input.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetPasswordVerifyForm(
        String token,
        @JsonProperty("new_password")
        String newPassword
) {}
