<<<<<<<< HEAD:src/main/java/com/gamevault/dto/output/UserStatsDTO.java
package com.gamevault.dto.output;
========
package com.gamevault.dto;
>>>>>>>> ea9ba89 (refactor: split form and dto objects; add achievement listener and patch controllers):src/main/java/com/gamevault/dto/UserStatsDTO.java

import com.gamevault.enums.Enums;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class UserStatsDTO {
    private String username;
    private long totalGames;
    private Map<Enums.status, Long> gamesByStatus;
    private Double averageRating;
    private long totalNotes;
}