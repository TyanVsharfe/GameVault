package com.gamevault.dto.igdb;

public record Company(
        int id,
        String name
) {
    public static record Cover(
            int id,
            String url
    ) {}
}
