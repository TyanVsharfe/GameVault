package com.gamevault.db.model.achievement;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "series_part")
@Getter
@Setter
@NoArgsConstructor
public class SeriesPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ElementCollection
    @CollectionTable(
            name = "series_part_games",
            joinColumns = @JoinColumn(name = "series_part_id")
    )
    @Column(name = "game_id")
    private Set<Long> gameIds = new HashSet<>();

    public SeriesPart(Set<Long> gameIds) {
        this.gameIds = gameIds;
    }
}
