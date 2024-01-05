package com.gamevault.db.model;

import com.gamevault.form.GameForm;
import jakarta.persistence.*;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long Id;
    private Long IGDB_ID;
    private String Title;
    private Double UserRating;
    @Lob
    private byte[] UserScreenshots;

    public Game() {

    }

    public Game(GameForm gameForm) {
        this.IGDB_ID = gameForm.getId();
        System.out.println("get id " + gameForm.getId());
        this.Title = gameForm.getTitle();
    }

    public void setId(Long id) {
        this.Id = id;
    }

    public Long getId() {
        return Id;
    }
}
