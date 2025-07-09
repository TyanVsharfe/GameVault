package com.gamevault.form.igdb;

import java.util.List;

public record FranchiseGame(
        int id,
        String name,
        Company.Cover cover,
        List<Platform> platforms,
        List<ReleaseDate> release_dates
) {
}
