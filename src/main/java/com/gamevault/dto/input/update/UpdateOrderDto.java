package com.gamevault.dto.input.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateOrderDto(
        @JsonProperty("igdb-id")
        long igdbId,
        int order
) {}
