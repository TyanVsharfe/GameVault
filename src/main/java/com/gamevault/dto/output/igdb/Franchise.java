<<<<<<<< HEAD:src/main/java/com/gamevault/dto/output/igdb/Franchise.java
package com.gamevault.dto.output.igdb;
========
package com.gamevault.dto.igdb;
>>>>>>>> ea9ba89 (refactor: split form and dto objects; add achievement listener and patch controllers):src/main/java/com/gamevault/dto/igdb/Franchise.java

import java.util.List;

public record Franchise(
        int id,
        String name,
        String slug,
        List<FranchiseGame> games
) {}
