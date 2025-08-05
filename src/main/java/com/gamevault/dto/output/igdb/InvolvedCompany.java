<<<<<<<< HEAD:src/main/java/com/gamevault/dto/output/igdb/InvolvedCompany.java
package com.gamevault.dto.output.igdb;
========
package com.gamevault.dto.igdb;
>>>>>>>> ea9ba89 (refactor: split form and dto objects; add achievement listener and patch controllers):src/main/java/com/gamevault/dto/igdb/InvolvedCompany.java

public record InvolvedCompany(
        int id,
        boolean developer,
        boolean publisher,
        Company company
) {}
