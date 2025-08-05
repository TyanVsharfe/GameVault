<<<<<<<< HEAD:src/main/java/com/gamevault/dto/output/igdb/Company.java
package com.gamevault.dto.output.igdb;
========
package com.gamevault.dto.igdb;
>>>>>>>> ea9ba89 (refactor: split form and dto objects; add achievement listener and patch controllers):src/main/java/com/gamevault/dto/igdb/Company.java

public record Company(
        int id,
        String name
) {
    public static record Cover(
            int id,
            String url
    ) {}
}
