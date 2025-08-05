<<<<<<<< HEAD:src/main/java/com/gamevault/dto/output/igdb/FranchiseGame.java
package com.gamevault.dto.output.igdb;
========
package com.gamevault.dto.igdb;
>>>>>>>> ea9ba89 (refactor: split form and dto objects; add achievement listener and patch controllers):src/main/java/com/gamevault/dto/igdb/FranchiseGame.java

import java.util.List;

public record FranchiseGame(
        int id,
        String name,
        Company.Cover cover,
        List<Platform> platforms,
        List<ReleaseDate> release_dates
) {}
