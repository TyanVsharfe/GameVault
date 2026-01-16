package com.gamevault.dto.output.igdb;

public record Company(
        int id,
        String name,
        String slug
) {
    public record Cover(
            int id,
            String url
    ) {}
}
