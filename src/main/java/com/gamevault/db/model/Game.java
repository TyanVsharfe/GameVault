package com.gamevault.db.model;

import com.gamevault.form.GameForm;
import jakarta.persistence.*;

@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;
    private Long igdbId;
    private String title;
    private Double userRating;
    private String coverUrl;
    @Lob
    private byte[] userScreenshots;

    public Game() {

    }

    public Game(GameForm gameForm) {
        this.igdbId = gameForm.id();
        System.out.println("get id " + gameForm.id());
        this.title = gameForm.title();
        this.coverUrl = gameForm.coverUrl();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getIgdbId() {
        return igdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
