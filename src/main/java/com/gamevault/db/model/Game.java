package com.gamevault.db.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gamevault.dto.input.GameForm;
import com.gamevault.dto.output.igdb.IgdbGameDTO;
import com.gamevault.enums.Enums;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "games")
public class Game {
    @Id
    @Column(name = "igdb_id")
    private Long igdbId;
    private String title;
    @Column(length = 512)
    private String coverUrl;
    @Lob
    private String description;
    private Enums.categoryIGDB category;

    @ManyToOne
    @JoinColumn(name = "parent_game_id")
    @JsonBackReference
    private Game parentGame;

    @OneToMany(mappedBy = "parentGame", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<Game> dlcs = new ArrayList<>();

    public Game(GameForm gameForm) {
        this.igdbId = gameForm.igdbId();
        this.title = gameForm.title();
        this.coverUrl = gameForm.coverUrl();
        this.description = gameForm.description();
        this.category = gameForm.category();
    }

    public Game(GameForm gameForm, Game game) {
        this.igdbId = gameForm.igdbId();
        this.title = gameForm.title();
        this.coverUrl = gameForm.coverUrl();
        this.description = gameForm.description();
        this.category = gameForm.category();
        this.parentGame = game;
    }

    public void addDlcs(List<Game> dlcs) {
        this.dlcs.addAll(dlcs);
    }
}
