package com.gamevault.dto.input.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.List;

public record UserGameListUpdateForm(
        @Nullable String name,
        @Nullable String description,
        @Nullable Boolean isPublic,
        @Nullable List<Long> games,
        @JsonProperty("new-order")
        @Nullable List<UpdateOrderDto> newOrder
) {}
