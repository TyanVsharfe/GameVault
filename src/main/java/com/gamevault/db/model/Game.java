package com.gamevault.db.model;

import com.gamevault.form.GameForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Game {
    @Id
    @Column(name = "igdb_id")
    private Long igdbId;
    private String title;
    @Column(length = 512)
    private String coverUrl;
    @Lob
    private String description;
//    @OneToMany(cascade = CascadeType.ALL)
//    private final List<Platform> platforms = new ArrayList<>();

    public Game(GameForm gameForm) {
        this.igdbId = gameForm.igdbId();
        this.title = gameForm.title();
        this.coverUrl = gameForm.coverUrl();
        this.description = gameForm.description();
    }
}
