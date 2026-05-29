package com.gamevault.dto.output.igdb;

public record Company(
        int id,
        String name,
        String slug,
        Cover cover
) {}
